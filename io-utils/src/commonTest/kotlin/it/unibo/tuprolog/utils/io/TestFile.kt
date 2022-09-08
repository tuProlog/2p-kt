package it.unibo.tuprolog.utils.io

import kotlin.test.Test
import kotlin.test.assertEquals

class TestFile {

    private val pathToFile = "/path/to/file.txt"

    @Test
    fun fileRepresentation() {
        assertEquals(
            pathToFile,
            File.of(pathToFile).toString()
        )
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
        assertEquals(
            pathToFile,
            File.of(pathToFile).path
        )
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
    fun fileRead() {
        assertEquals(
            ExampleFiles.PARENTS,
            findResource("Parents.pl").toFile().readText().trimEnd()
        )
    }
}
