package org.gnit.lucenekmp.cli

import org.gnit.lucenekmp.store.ByteBuffersDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LuceneCliEngineTest {
    private val fields = listOf(
        FieldConfig("title", boost = 5f),
        FieldConfig("body", store = false),
    )

    @Test
    fun indexesAndSearchesConfiguredFields() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            engine.add(SearchDocument("grace", mapOf("title" to "Grace Church", "body" to "Lasting hope")))
            engine.add(SearchDocument("search", mapOf("title" to "Search notes", "body" to "Fast full text search")))

            val result = engine.search("hope").single()

            assertEquals("grace", result.id)
            assertEquals(mapOf("title" to "Grace Church"), result.fields)
        }
    }

    @Test
    fun usesEnglishAnalyzerStemming() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            engine.add(SearchDocument("runner", mapOf("title" to "Runner", "body" to "jumped quickly")))

            assertEquals("runner", engine.search("jumping").single().id)
        }
    }

    @Test
    fun createReplacesExistingIndex() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            engine.add(SearchDocument("old", mapOf("body" to "removed")))
            engine.create()

            assertEquals(emptyList(), engine.search("removed"))
        }
    }

    @Test
    fun rejectsBlankDocumentsAndQueries() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            assertFailsWith<IllegalArgumentException> { engine.add(SearchDocument("", mapOf("body" to "x"))) }
            assertFailsWith<IllegalArgumentException> { engine.add(SearchDocument("id", mapOf("body" to ""))) }
            assertFailsWith<IllegalArgumentException> { engine.search("") }
        }
    }

    @Test
    fun parallelIndexHasSameContentsAsSerialIndex() {
        val documents = (1..100).map { number ->
            SearchDocument(
                id = "document-$number",
                fields = mapOf("title" to "Church $number", "body" to "shared searchable content $number"),
            )
        }

        val serialIds = ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            engine.addAll(documents.asSequence())
            engine.search("shared", documents.size).map(SearchResult::id).sorted()
        }

        val parallelIds = ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            val report = engine.addAllConcurrent(
                sources = documents.map { document -> DocumentSource(document.id) { document } },
                workers = 8,
            )
            assertEquals(documents.size, report.indexed)
            assertEquals(0, report.skipped)
            assertEquals(0, report.errors)
            engine.search("shared", documents.size).map(SearchResult::id).sorted()
        }

        assertEquals(serialIds, parallelIds)
    }

    @Test
    fun sharedIndexWriterHandlesConcurrentStress() {
        val documentCount = 500
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            val report = engine.addAllConcurrent(
                sources = (1..documentCount).map { number ->
                    DocumentSource("stress-$number") {
                        SearchDocument(
                            "stress-$number",
                            mapOf("title" to "Stress $number", "body" to "concurrent shared marker"),
                        )
                    }
                },
                workers = 8,
            )

            assertEquals(documentCount, report.indexed)
            assertEquals(0, report.errors)
            assertEquals(documentCount, engine.search("marker", documentCount).size)
        }
    }

    @Test
    fun concurrentFailuresAreReportedDeterministicallyAfterCompletion() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory, fields)
            engine.create()
            val report = engine.addAllConcurrent(
                sources = listOf(
                    DocumentSource("z-file") { error("z failure") },
                    DocumentSource("skipped") { null },
                    DocumentSource("a-file") { error("a failure") },
                    DocumentSource("ok") {
                        SearchDocument("ok", mapOf("title" to "Okay", "body" to "valid document"))
                    },
                ),
                workers = 4,
            )

            assertEquals(1, report.indexed)
            assertEquals(1, report.skipped)
            assertEquals(2, report.errors)
            assertEquals(listOf("a-file", "z-file"), report.failures.map(IndexingFailure::id))
            assertTrue(report.failures.all { it.message.endsWith("failure") })
        }
    }

    @Test
    fun defaultWorkersArePositiveAndCapped() {
        assertTrue(defaultWorkerCount() in 1..MAX_DEFAULT_WORKERS)
    }
}
