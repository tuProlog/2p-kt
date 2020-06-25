package it.unibo.tuprolog.serialize

import kotlin.test.Test
import kotlin.test.assertEquals

class TestTermSerializer {

    @Test
    fun testAtomSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("\"atom\"") {
            atomOf("atom")
        }

        serializer.assertTermSerializationWorks("\"an atom\"") {
            atomOf("an atom")
        }
    }

    @Test
    fun testAtomSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        serializer.assertTermSerializationWorks("--- \"atom\"\n") {
            atomOf("atom")
        }
        serializer.assertTermSerializationWorks("--- \"an atom\"\n") {
            atomOf("an atom")
        }
    }

    @Test
    fun testNumericSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("2") {
            numOf(2)
        }

        serializer.assertTermSerializationWorks("3.0") {
            numOf(3.0)
        }
    }

    @Test
    fun testNumericSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        serializer.assertTermSerializationWorks("--- 3\n") {
            numOf(3)
        }
        serializer.assertTermSerializationWorks("--- 4.2\n") {
            numOf(4.2)
        }
    }

    @Test
    fun testListSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("{\"list\":[\"hello\",false]}") {
            listOf(atomOf("hello"), truthOf(false))
        }

        serializer.assertTermSerializationWorks("{\"list\":[\"hello\",1]}") {
            listOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testListSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        var expected = """---
                |list:
                |- "hello"
                |- false
                |
                """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            listOf(atomOf("hello"), truthOf(false))
        }

        expected = """---
                |list:
                |- "hello"
                |- 1
                |
                """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            listOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testSetSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("{\"set\":[\"hello\",1]}") {
            setOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testSetSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        var expected = """---
                |set:
                |- "hello"
                |- false
                |
                """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            setOf(atomOf("hello"), truthOf(false))
        }

        expected = """---
                |set:
                |- "hello"
                |- 1
                |
                """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            setOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testStructSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("{\"fun\":\"f\",\"args\":[\"hello\",2]}") {
            structOf("f", atomOf("hello"), numOf(2))
        }
    }

    @Test
    fun testStructSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        val expected = """---
                |fun: "f"
                |args:
                |- "hello"
                |- 2
                |- list:
                |  - true
                |  - "ciao"
                |
                """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            structOf("f", atomOf("hello"), numOf(2), listOf(truthOf(true), atomOf("ciao")))
        }
    }

    @Test
    fun testConstantSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("true") {
            truthOf(true)
        }

        serializer.assertTermSerializationWorks("false") {
            truthOf(false)
        }
    }

    @Test
    fun testConstantSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        serializer.assertTermSerializationWorks("--- true\n") {
            truthOf(true)
        }
        serializer.assertTermSerializationWorks("--- false\n") {
            truthOf(false)
        }
    }

    @Test
    fun testVariablesSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)


        serializer.assertTermSerializationWorks("{\"var\":\"Y\"}") {
            varOf("Y")
        }

        serializer.assertTermSerializationWorks("{\"var\":\"Incognita\"}") {
            varOf("Incognita")
        }
    }

    @Test
    fun testVariablesSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, serializer.mimeType)

        val expected = """---
                |var: "Y"
                |
                """.trimMargin()

        serializer.assertTermSerializationWorks(expected) {
            varOf("Y")
        }
    }
}