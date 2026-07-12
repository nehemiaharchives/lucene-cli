package org.gnit.lucenekmp.cli

import okio.FileSystem
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains

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
}
