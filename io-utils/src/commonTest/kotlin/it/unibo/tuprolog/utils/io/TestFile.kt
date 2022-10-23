package it.unibo.tuprolog.utils.io

import it.unibo.tuprolog.Os
import it.unibo.tuprolog.currentOs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestFile {

    private val pathToFile = "/path/to/file.txt"

    @Test
    fun fileRepresentation() {
        try {
            assertEquals(
                pathToFile,
                File.of(pathToFile).toString()
            )
        } catch (_: AssertionError) {
            assertEquals(Os.WINDOWS, currentOs())
            assertEndsWith(
                pathToFile.toWindowsPath(),
                File.of(pathToFile).toString()
            )
        }
    }

    @Test
    fun fileName() {
        assertEquals(
            "file.txt",
            File.of(pathToFile).name
        )
    }

    @Test
    fun filePath() {
        try {
            assertEquals(
                pathToFile,
                File.of(pathToFile).path
            )
        } catch (_: AssertionError) {
            assertEquals(Os.WINDOWS, currentOs())
            assertEndsWith(
                pathToFile.toWindowsPath(),
                File.of(pathToFile).path
            )
        }
    }

    @Test
    fun fileRename() {
        assertEquals(
            File.of("/path/to/anotherFile.exe"),
            File.of(pathToFile).rename("anotherFile.exe")
        )
    }

    @Test
    fun fileParent() {
        assertEquals(
            File.of("/path/to"),
            File.of(pathToFile).parent
        )
    }

    @Test
    fun subPath() {
        try {
            assertEquals(
                File.of(pathToFile),
                File.of("/") / "path" / "to" / "file.txt"
            )
        } catch (_: AssertionError) {
            assertEquals(Os.WINDOWS, currentOs())
            assertEndsWith(
                pathToFile.toWindowsPath(),
                (File.of("/") / "path" / "to" / "file.txt").path
            )
        }
    }

    @Test
    fun fileRead() {
        assertEquals(
            ExampleFiles.PARENTS,
            findResource("Parents.pl").toFile().readText().ensureUnixLineTermination()
        )
    }

    @Test
    fun testIO() {
        val tmp = File.temp("test", "txt")
        val content = "ciao"
        tmp.writeText(content)
        assertEquals(content, tmp.readText())
    }

    private fun assertEndsWith(expectedEnding: String, actual: String) {
        assertTrue(actual.endsWith(expectedEnding), "String \"$actual\" is not ending with $expectedEnding")
    }

    private fun String.toWindowsPath(): String =
        trimEnd().replace("/", "\\")

    private fun String.ensureUnixLineTermination(): String =
        trimEnd().replace("\r\n", "\n")
}
