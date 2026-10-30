package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import okio.FileSystem
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 * End-to-end coverage for the `open/3,4`-backed local file write and append behavior (via
 * [Url.openOutputChannel]/[Url.openInputChannel], exactly what the `open` primitives use), against
 * a real temp file on the host file system. This behavior had no test coverage at all before
 * issue #923 and doubles as the "JVM smoke suite against a real temp directory" it asks for
 * (this also runs for real, against Node's file system, under `:io-lib:jsNodeTest`).
 */
class OpenLocalFileSpec :
    FunSpec({
        val createdUrls = mutableListOf<Url>()

        fun ensureRealFileSystem() {
            // Regardless of what other test classes left `LocalFileSystem.fileSystem` set to.
            LocalFileSystem.fileSystem = platformFileSystem
        }

        fun cleanUp() {
            createdUrls.forEach { LocalFileSystem.fileSystem.delete(it.toLocalPath(), mustExist = false) }
            createdUrls.clear()
        }

        fun tempFileUrl(prefix: String): Url {
            val name = "2p-kt-io-lib-test-$prefix-${Random.nextInt()}.tmp"
            val url = Url.file((FileSystem.SYSTEM_TEMPORARY_DIRECTORY / name).toString())
            createdUrls += url
            return url
        }

        fun Url.readAllViaChannel(): String = openInputChannel().use { generateSequence { read() }.joinToString("") }

        beforeTest { ensureRealFileSystem() }

        afterTest { cleanUp() }

        test("open-for-write-then-read-back-through-real-local-file-system") {
            val url = tempFileUrl("write-read")

            url.openOutputChannel(append = false).use {
                write("hello from open(File, write, S)")
                flush()
            }

            assertEquals("hello from open(File, write, S)", url.readAllViaChannel())
        }

        test("open-for-write-truncates-existing-content") {
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

        test("open-for-append-adds-to-existing-content") {
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
    })
