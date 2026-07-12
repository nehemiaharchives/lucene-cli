package org.gnit.lucenekmp.cli

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
                require(document.id.isNotBlank()) { "document id must not be blank" }
                require(document.fields.values.any { it.isNotBlank() }) { "document fields must not be blank" }
                writer.addDocument(Document().apply {
                    add(StringField("id", document.id, Field.Store.YES))
                    fieldConfigs.forEach { config ->
                        document.fields[config.name]?.takeIf { it.isNotBlank() }?.let { value ->
                            add(TextField(config.name, value, if (config.store) Field.Store.YES else Field.Store.NO))
                        }
                    }
                })
                count++
            }
        }
        return count
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
}
