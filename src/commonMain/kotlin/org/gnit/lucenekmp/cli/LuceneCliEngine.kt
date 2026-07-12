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
import org.gnit.lucenekmp.queryparser.classic.QueryParser
import org.gnit.lucenekmp.search.IndexSearcher
import org.gnit.lucenekmp.store.Directory

data class EnglishDocument(val id: String, val body: String)

data class SearchResult(val id: String, val body: String, val score: Float)

fun String.toAnalyzer(): Analyzer = when (this) {
    "EnglishAnalyzer" -> EnglishAnalyzer()
    else -> throw IllegalArgumentException("$this is not supported; use EnglishAnalyzer")
}

class LuceneCliEngine(
    private val directory: Directory,
    private val analyzer: Analyzer = EnglishAnalyzer(),
) {
    fun create() {
        val config = IndexWriterConfig(analyzer).apply {
            openMode = IndexWriterConfig.OpenMode.CREATE
        }
        IndexWriter(directory, config).use { }
    }

    fun add(document: EnglishDocument) {
        require(document.id.isNotBlank()) { "document id must not be blank" }
        require(document.body.isNotBlank()) { "document body must not be blank" }

        IndexWriter(directory, IndexWriterConfig(analyzer)).use { writer ->
            writer.addDocument(Document().apply {
                add(StringField("id", document.id, Field.Store.YES))
                add(TextField("body", document.body, Field.Store.YES))
            })
        }
    }

    fun search(queryText: String, limit: Int = 10): List<SearchResult> {
        require(queryText.isNotBlank()) { "query must not be blank" }
        require(limit > 0) { "limit must be positive" }

        StandardDirectoryReader.open(directory, null, null).use { reader ->
            val searcher = IndexSearcher(reader)
            val query = QueryParser("body", analyzer).parse(queryText)
                ?: return emptyList()
            val storedFields = searcher.storedFields()
            return searcher.search(query, limit).scoreDocs.map { scoreDoc ->
                val document = storedFields.document(scoreDoc.doc)
                SearchResult(
                    id = requireNotNull(document.get("id")),
                    body = requireNotNull(document.get("body")),
                    score = scoreDoc.score,
                )
            }
        }
    }
}
