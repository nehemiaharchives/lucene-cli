package org.gnit.lucenekmp.cli

import org.gnit.lucenekmp.store.ByteBuffersDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LuceneCliEngineTest {
    @Test
    fun indexesAndSearchesEnglishDocuments() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory)
            engine.create()
            engine.add(EnglishDocument("grace", "Grace brings lasting hope."))
            engine.add(EnglishDocument("search", "Lucene provides fast full text search."))

            val results = engine.search("hope")

            assertEquals(1, results.size)
            assertEquals("grace", results.single().id)
            assertEquals("Grace brings lasting hope.", results.single().body)
        }
    }

    @Test
    fun usesEnglishAnalyzerStemmingForIndexAndQuery() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory)
            engine.create()
            engine.add(EnglishDocument("runner", "The runner jumped quickly."))

            assertEquals("runner", engine.search("jumping").single().id)
        }
    }

    @Test
    fun createReplacesAnExistingIndex() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory)
            engine.create()
            engine.add(EnglishDocument("old", "This document will be removed."))

            engine.create()

            assertEquals(emptyList(), engine.search("removed"))
        }
    }

    @Test
    fun rejectsBlankDocumentsAndQueries() {
        ByteBuffersDirectory().use { directory ->
            val engine = LuceneCliEngine(directory)
            engine.create()

            assertFailsWith<IllegalArgumentException> {
                engine.add(EnglishDocument("", "body"))
            }
            assertFailsWith<IllegalArgumentException> {
                engine.add(EnglishDocument("id", ""))
            }
            assertFailsWith<IllegalArgumentException> {
                engine.search("")
            }
        }
    }
}
