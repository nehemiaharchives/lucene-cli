package org.gnit.lucenekmp.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceIndexerTest {
    @Test
    fun extractsProjectDefinedFields() {
        val content = """<meta content="Grace Church · Tokyo" property="og:title">"""
        val name = FieldConfig("name", pattern = """content="([^"]+) · [^"]+""", group = 1)
        val address = FieldConfig("address", pattern = """content="[^"]+ · ([^"]+)""", group = 1)

        assertEquals("Grace Church", extractField(name, "place.html", content))
        assertEquals("Tokyo", extractField(address, "place.html", content))
    }

    @Test
    fun extractsAllMatchesAndPathFields() {
        val tags = FieldConfig("tags", pattern = """tag:([a-z]+)""", matchAll = true)
        val path = FieldConfig("path", source = "path", pattern = null)

        assertEquals("church tokyo", extractField(tags, "places/one.txt", "tag:church tag:tokyo"))
        assertEquals("places/one.txt", extractField(path, "places/one.txt", "ignored"))
    }
}
