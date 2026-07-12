package org.gnit.lucenekmp.cli

import org.gnit.lucenekmp.store.ByteBuffersDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
}
