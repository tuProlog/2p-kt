package it.unibo.tuprolog.solve.libs.io

import io.kotest.core.spec.style.FunSpec
import okio.FileNotFoundException
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Exercises [LocalFileSystem] directly, deterministically, against an in-memory
 * [FakeFileSystem], per the "Common tests" requirements of issue #923.
 */
class LocalFileSystemSpec :
    FunSpec({
        lateinit var fake: FakeFileSystem

        fun setUp() {
            fake = FakeFileSystem()
            LocalFileSystem.fileSystem = fake
        }

        fun tearDown() {
            fake.checkNoOpenFiles()
            LocalFileSystem.fileSystem = platformFileSystem
        }

        beforeTest { setUp() }

        afterTest { tearDown() }

        test("read-existing-text-file") {
            val path = "/parents.pl".toPath()
            fake.write(path) { writeUtf8("male(james1).\nfemale(catherine).\n") }

            val text = LocalFileSystem.source(path).buffer().use { it.readUtf8() }

            assertEquals("male(james1).\nfemale(catherine).\n", text)
        }

        test("read-bytes-from-existing-file") {
            val path = "/random.bin".toPath()
            val bytes = byteArrayOf(1, 2, 3, 4, 5, -1, -2)
            fake.write(path) { write(bytes) }

            val read = LocalFileSystem.source(path).buffer().use { it.readByteArray() }

            assertEquals(bytes.toList(), read.toList())
        }

        test("create-and-write-file") {
            val path = "/created.txt".toPath()

            LocalFileSystem.sink(path).buffer().use { it.writeUtf8("hello") }

            assertEquals("hello", fake.read(path) { readUtf8() })
        }

        test("overwrite-truncates-existing-file") {
            val path = "/overwritten.txt".toPath()
            fake.write(path) { writeUtf8("a much longer original content") }

            LocalFileSystem.sink(path).buffer().use { it.writeUtf8("short") }

            assertEquals("short", fake.read(path) { readUtf8() })
        }

        test("append-to-existing-file") {
            val path = "/appended.txt".toPath()
            fake.write(path) { writeUtf8("abc") }

            LocalFileSystem.appendingSink(path).buffer().use { it.writeUtf8("def") }

            assertEquals("abcdef", fake.read(path) { readUtf8() })
        }

        test("flush-output") {
            val path = "/flushed.txt".toPath()
            val sink = LocalFileSystem.sink(path).buffer()

            sink.writeUtf8("first-")
            sink.flush()
            sink.writeUtf8("second")
            sink.close()

            assertEquals("first-second", fake.read(path) { readUtf8() })
        }

        test("close-closes-the-underlying-stream") {
            val path = "/closed.txt".toPath()
            val sink = LocalFileSystem.sink(path).buffer()

            sink.close()

            assertFailsWith<IllegalStateException> { sink.writeUtf8("too late") }
        }

        test("missing-file-fails-to-open-for-reading") {
            assertFailsWith<FileNotFoundException> {
                LocalFileSystem.source("/does/not/exist.pl".toPath())
            }
        }

        test("invalid-path-fails-to-open-for-writing") {
            assertFailsWith<FileNotFoundException> {
                LocalFileSystem.sink("/no/such/directory/file.txt".toPath())
            }
        }
    })
