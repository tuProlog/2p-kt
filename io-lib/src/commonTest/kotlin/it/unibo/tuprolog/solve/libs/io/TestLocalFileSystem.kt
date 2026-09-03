package it.unibo.tuprolog.solve.libs.io

import okio.FileNotFoundException
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Exercises [LocalFileSystem] directly, deterministically, against an in-memory
 * [FakeFileSystem], per the "Common tests" requirements of issue #923.
 */
class TestLocalFileSystem {
    private lateinit var fake: FakeFileSystem

    @BeforeTest
    fun setUp() {
        fake = FakeFileSystem()
        LocalFileSystem.fileSystem = fake
    }

    @AfterTest
    fun tearDown() {
        fake.checkNoOpenFiles()
        LocalFileSystem.fileSystem = platformFileSystem
    }

    @Test
    fun testReadExistingTextFile() {
        val path = "/parents.pl".toPath()
        fake.write(path) { writeUtf8("male(james1).\nfemale(catherine).\n") }

        val text = LocalFileSystem.source(path).buffer().use { it.readUtf8() }

        assertEquals("male(james1).\nfemale(catherine).\n", text)
    }

    @Test
    fun testReadBytesFromExistingFile() {
        val path = "/random.bin".toPath()
        val bytes = byteArrayOf(1, 2, 3, 4, 5, -1, -2)
        fake.write(path) { write(bytes) }

        val read = LocalFileSystem.source(path).buffer().use { it.readByteArray() }

        assertEquals(bytes.toList(), read.toList())
    }

    @Test
    fun testCreateAndWriteFile() {
        val path = "/created.txt".toPath()

        LocalFileSystem.sink(path).buffer().use { it.writeUtf8("hello") }

        assertEquals("hello", fake.read(path) { readUtf8() })
    }

    @Test
    fun testOverwriteTruncatesExistingFile() {
        val path = "/overwritten.txt".toPath()
        fake.write(path) { writeUtf8("a much longer original content") }

        LocalFileSystem.sink(path).buffer().use { it.writeUtf8("short") }

        assertEquals("short", fake.read(path) { readUtf8() })
    }

    @Test
    fun testAppendToExistingFile() {
        val path = "/appended.txt".toPath()
        fake.write(path) { writeUtf8("abc") }

        LocalFileSystem.appendingSink(path).buffer().use { it.writeUtf8("def") }

        assertEquals("abcdef", fake.read(path) { readUtf8() })
    }

    @Test
    fun testFlushOutput() {
        val path = "/flushed.txt".toPath()
        val sink = LocalFileSystem.sink(path).buffer()

        sink.writeUtf8("first-")
        sink.flush()
        sink.writeUtf8("second")
        sink.close()

        assertEquals("first-second", fake.read(path) { readUtf8() })
    }

    @Test
    fun testCloseClosesTheUnderlyingStream() {
        val path = "/closed.txt".toPath()
        val sink = LocalFileSystem.sink(path).buffer()

        sink.close()

        assertFailsWith<IllegalStateException> { sink.writeUtf8("too late") }
    }

    @Test
    fun testMissingFileFailsToOpenForReading() {
        assertFailsWith<FileNotFoundException> {
            LocalFileSystem.source("/does/not/exist.pl".toPath())
        }
    }

    @Test
    fun testInvalidPathFailsToOpenForWriting() {
        assertFailsWith<FileNotFoundException> {
            LocalFileSystem.sink("/no/such/directory/file.txt".toPath())
        }
    }
}
