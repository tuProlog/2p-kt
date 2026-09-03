package it.unibo.tuprolog.solve.libs.io

import okio.FileSystem
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * End-to-end coverage for the `open/3,4`-backed local file write and append behavior (via
 * [Url.openOutputChannel]/[Url.openInputChannel], exactly what the `open` primitives use), against
 * a real temp file on the host file system. This behavior had no test coverage at all before
 * issue #923 and doubles as the "JVM smoke suite against a real temp directory" it asks for
 * (this also runs for real, against Node's file system, under `:io-lib:jsNodeTest`).
 */
class TestOpenLocalFile {
    private val createdUrls = mutableListOf<Url>()

    @BeforeTest
    fun ensureRealFileSystem() {
        // Regardless of what other test classes left `LocalFileSystem.fileSystem` set to.
        LocalFileSystem.fileSystem = platformFileSystem
    }

    @AfterTest
    fun cleanUp() {
        createdUrls.forEach { LocalFileSystem.fileSystem.delete(it.toLocalPath(), mustExist = false) }
        createdUrls.clear()
    }

    private fun tempFileUrl(prefix: String): Url {
        val name = "2p-kt-io-lib-test-$prefix-${Random.nextInt()}.tmp"
        val url = Url.file((FileSystem.SYSTEM_TEMPORARY_DIRECTORY / name).toString())
        createdUrls += url
        return url
    }

    private fun Url.readAllViaChannel(): String =
        openInputChannel().use { generateSequence { read() }.joinToString("") }

    @Test
    fun testOpenForWriteThenReadBackThroughRealLocalFileSystem() {
        val url = tempFileUrl("write-read")

        url.openOutputChannel(append = false).use {
            write("hello from open(File, write, S)")
            flush()
        }

        assertEquals("hello from open(File, write, S)", url.readAllViaChannel())
    }

    @Test
    fun testOpenForWriteTruncatesExistingContent() {
        val url = tempFileUrl("truncate")

        url.openOutputChannel(append = false).use {
            write("a much longer original content")
            flush()
        }
        url.openOutputChannel(append = false).use {
            write("short")
            flush()
        }

        assertEquals("short", url.readAllViaChannel())
    }

    @Test
    fun testOpenForAppendAddsToExistingContent() {
        val url = tempFileUrl("append")

        url.openOutputChannel(append = false).use {
            write("abc")
            flush()
        }
        url.openOutputChannel(append = true).use {
            write("def")
            flush()
        }

        assertEquals("abcdef", url.readAllViaChannel())
    }
}
