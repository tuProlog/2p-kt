package it.unibo.tuprolog.solve.libs.io

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.FileNotFoundException
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use

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

            text shouldBe "male(james1).\nfemale(catherine).\n"
        }

        test("read-bytes-from-existing-file") {
            val path = "/random.bin".toPath()
            val bytes = byteArrayOf(1, 2, 3, 4, 5, -1, -2)
            fake.write(path) { write(bytes) }

            val read = LocalFileSystem.source(path).buffer().use { it.readByteArray() }

            read.toList() shouldBe bytes.toList()
        }

        test("create-and-write-file") {
            val path = "/created.txt".toPath()

            LocalFileSystem.sink(path).buffer().use { it.writeUtf8("hello") }

            (fake.read(path) { readUtf8() }) shouldBe "hello"
        }

        test("overwrite-truncates-existing-file") {
            val path = "/overwritten.txt".toPath()
            fake.write(path) { writeUtf8("a much longer original content") }

            LocalFileSystem.sink(path).buffer().use { it.writeUtf8("short") }

            (fake.read(path) { readUtf8() }) shouldBe "short"
        }

        test("append-to-existing-file") {
            val path = "/appended.txt".toPath()
            fake.write(path) { writeUtf8("abc") }

            LocalFileSystem.appendingSink(path).buffer().use { it.writeUtf8("def") }

            (fake.read(path) { readUtf8() }) shouldBe "abcdef"
        }

        test("flush-output") {
            val path = "/flushed.txt".toPath()
            val sink = LocalFileSystem.sink(path).buffer()

            sink.writeUtf8("first-")
            sink.flush()
            sink.writeUtf8("second")
            sink.close()

            (fake.read(path) { readUtf8() }) shouldBe "first-second"
        }

        test("close-closes-the-underlying-stream") {
            val path = "/closed.txt".toPath()
            val sink = LocalFileSystem.sink(path).buffer()

            sink.close()

            shouldThrow<IllegalStateException> { sink.writeUtf8("too late") }
        }

        test("missing-file-fails-to-open-for-reading") {
            shouldThrow<FileNotFoundException> {
                LocalFileSystem.source("/does/not/exist.pl".toPath())
            }
        }

        test("invalid-path-fails-to-open-for-writing") {
            shouldThrow<FileNotFoundException> {
                LocalFileSystem.sink("/no/such/directory/file.txt".toPath())
            }
        }
    })
