package org.gnit.lucenekmp.cli

import okio.FileSystem
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class MainTest {

    lateinit var fakeFs: FileSystem

    @BeforeTest
    fun setup(){
        fakeFs = FakeFileSystem()
    }

    @Test
    fun lcWithNoArgument(){
        val result = Lc(fileSystem = fakeFs).test(emptyList())
        assertContains(result.stdout, "Usage: lc")
    }

    @Test
    fun unsupportedAnalyzerIsRejected() {
        val error = kotlin.runCatching { "MissingAnalyzer".toAnalyzer() }.exceptionOrNull()

        assertContains(error?.message.orEmpty(), "Available analyzers:")
        assertContains(error?.message.orEmpty(), "StandardAnalyzer")
    }

    @Test
    fun everyRegisteredAnalyzerCanBeCreated() {
        supportedAnalyzerNames.forEach { name ->
            name.toAnalyzer().close()
        }
    }
}
