@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package org.gnit.lucenekmp.cli

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.concurrent.atomics.incrementAndFetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.gnit.lucenekmp.analysis.Analyzer
import org.gnit.lucenekmp.analysis.en.EnglishAnalyzer
import org.gnit.lucenekmp.document.Document
import org.gnit.lucenekmp.document.Field
import org.gnit.lucenekmp.document.StringField
import org.gnit.lucenekmp.document.TextField
import org.gnit.lucenekmp.index.IndexWriter
import org.gnit.lucenekmp.index.IndexWriterConfig
import org.gnit.lucenekmp.index.StandardDirectoryReader
import org.gnit.lucenekmp.queryparser.classic.MultiFieldQueryParser
import org.gnit.lucenekmp.search.IndexSearcher
import org.gnit.lucenekmp.store.Directory

data class SearchDocument(val id: String, val fields: Map<String, String>)

data class SearchResult(val id: String, val fields: Map<String, String>, val score: Float)

data class DocumentSource(
    val id: String,
    val load: () -> SearchDocument?,
)

data class IndexingFailure(
    val id: String,
    val message: String,
)

data class IndexingReport(
    val indexed: Int,
    val skipped: Int,
    val errors: Int,
    val failures: List<IndexingFailure>,
)

class LuceneCliEngine(
    private val directory: Directory,
    private val fieldConfigs: List<FieldConfig> = listOf(FieldConfig("body")),
    private val analyzer: Analyzer = EnglishAnalyzer(),
) {
    fun create() {
        val config = IndexWriterConfig(analyzer).apply { openMode = IndexWriterConfig.OpenMode.CREATE }
        IndexWriter(directory, config).use { }
    }

    fun add(document: SearchDocument) = addAll(sequenceOf(document))

    fun addAll(documents: Sequence<SearchDocument>): Int {
        var count = 0
        IndexWriter(directory, IndexWriterConfig(analyzer)).use { writer ->
            documents.forEach { document ->
                writer.addDocument(document.toLuceneDocument())
                count++
            }
        }
        return count
    }

    /**
     * Loads and indexes documents concurrently using one shared [IndexWriter]. Each worker performs
     * the complete load, extraction, and addDocument path so tokenization is not serialized behind
     * a single consumer.
     */
    fun addAllConcurrent(sources: List<DocumentSource>, workers: Int): IndexingReport {
        require(workers > 0) { "workers must be positive" }

        val nextSource = AtomicInt(0)
        val indexed = AtomicInt(0)
        val skipped = AtomicInt(0)
        val errors = AtomicInt(0)
        val failures = mutableListOf<IndexingFailure>()
        val failuresMutex = Mutex()
        val workerCount = workers.coerceAtMost(sources.size.coerceAtLeast(1))

        IndexWriter(directory, IndexWriterConfig(analyzer)).use { writer ->
            runBlocking {
                val dispatcher = Dispatchers.Default.limitedParallelism(workerCount)
                List(workerCount) {
                    async(dispatcher) {
                        while (true) {
                            val sourceIndex = nextSource.fetchAndIncrement()
                            if (sourceIndex >= sources.size) break
                            val source = sources[sourceIndex]
                            try {
                                val document = source.load()
                                if (document == null) {
                                    skipped.incrementAndFetch()
                                } else {
                                    writer.addDocument(document.toLuceneDocument())
                                    indexed.incrementAndFetch()
                                }
                            } catch (failure: Throwable) {
                                errors.incrementAndFetch()
                                failuresMutex.withLock {
                                    failures += IndexingFailure(
                                        id = source.id,
                                        message = failure.message ?: "unknown indexing error",
                                    )
                                }
                            }
                        }
                    }
                }.awaitAll()
            }
        }

        val deterministicFailures = failures.sortedWith(compareBy(IndexingFailure::id, IndexingFailure::message))
        return IndexingReport(
            indexed = indexed.load(),
            skipped = skipped.load(),
            errors = errors.load(),
            failures = deterministicFailures,
        )
    }

    fun search(queryText: String, limit: Int = 10): List<SearchResult> {
        require(queryText.isNotBlank()) { "query must not be blank" }
        require(limit > 0) { "limit must be positive" }
        val searchableFields = fieldConfigs.map { it.name }.toTypedArray()
        val boosts = fieldConfigs.associate { it.name to it.boost }
        StandardDirectoryReader.open(directory, null, null).use { reader ->
            val searcher = IndexSearcher(reader)
            val query = MultiFieldQueryParser(searchableFields, analyzer, boosts).parse(queryText)
                ?: return emptyList()
            val storedFields = searcher.storedFields()
            return searcher.search(query, limit).scoreDocs.map { scoreDoc ->
                val document = storedFields.document(scoreDoc.doc)
                SearchResult(
                    id = requireNotNull(document.get("id")),
                    fields = fieldConfigs.filter { it.store }.associate { it.name to (document.get(it.name) ?: "") },
                    score = scoreDoc.score,
                )
            }
        }
    }

    private fun SearchDocument.toLuceneDocument(): Document {
        require(id.isNotBlank()) { "document id must not be blank" }
        require(fields.values.any { it.isNotBlank() }) { "document fields must not be blank" }
        return Document().apply {
            add(StringField("id", id, Field.Store.YES))
            fieldConfigs.forEach { config ->
                fields[config.name]?.takeIf { it.isNotBlank() }?.let { value ->
                    add(TextField(config.name, value, if (config.store) Field.Store.YES else Field.Store.NO))
                }
            }
        }
    }
}
