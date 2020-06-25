package it.unibo.tuprolog.serialize

import kotlin.test.Test
import kotlin.test.assertEquals

class TestTermDeserializer {
    @Test
    fun testAtomDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("\"hello\"") {
            atomOf("hello")
        }
        deserializer.assertTermDeserializationWorks("\"other atom\"") {
            atomOf("other atom")
        }

    }

    @Test
    fun testAtomDeserializationInYaml() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("--- \"hello\"") {
            atomOf("hello")
        }
        deserializer.assertTermDeserializationWorks("--- \"other atom\"") {
            atomOf("other atom")
        }

    }

    @Test
    fun testNumericDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("2") {
            numOf(2)
        }
        deserializer.assertTermDeserializationWorks("3.1") {
            numOf(3.1)
        }
    }

    @Test
    fun testNumericDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("--- 2") {
            numOf(2)
        }
        deserializer.assertTermDeserializationWorks("--- 3.1") {
            numOf(3.1)
        }
    }

    @Test
    fun testListDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("{\"list\":[\"hello\",1,true]}") {
            listOf(atomOf("hello"), numOf(1), truthOf(true))
        }
    }

    @Test
    fun testListDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        var actual = """---
                |list:
                |- "hello"
                |- 1
                |- true
                |
                """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            listOf(atomOf("hello"), numOf(1), truthOf(true))
        }
    }

    @Test
    fun testSetDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("{\"set\":[\"hello\",1, false]}") {
            setOf(atomOf("hello"), numOf(1), truthOf(false))
        }
    }

    @Test
    fun testSetDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        var actual = """---
                |set:
                |- "hello"
                |- 1
                |- true
                |
                """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            setOf(atomOf("hello"), numOf(1), truthOf(true))
        }
    }

    @Test
    fun testStructDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("{\"fun\":\"f\",\"args\":[\"hello\",2]}") {
            structOf("f", atomOf("hello"), numOf(2))
        }

        deserializer.assertTermDeserializationWorks("{\"fun\":\"f\",\"args\":[\"prova 2\",3.0,{\"list\":[\"qua ci va una lista\",true]}]}") {
            structOf(
                "f", atomOf("prova 2"), numOf(3.0),
                listOf(atomOf("qua ci va una lista"), truthOf(true))
            )
        }
    }

    @Test
    fun testStructDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        var actual = """---
                |fun: "f"
                |args:
                |- "hello"
                |- 2
                |
                """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            structOf("f", atomOf("hello"), numOf(2))
        }

        actual = """---
                |fun: "f"
                |args:
                |- "prova 2"
                |- 3.0
                |- list:
                |  - "qua ci va una lista"
                |  - true
                |
                """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            structOf(
                "f", atomOf("prova 2"), numOf(3.0),
                listOf(atomOf("qua ci va una lista"), truthOf(true))
            )
        }
    }

    @Test
    fun testVariablesDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)

        assertEquals(MimeType.Json, deserializer.mimeType)

        deserializer.assertTermDeserializationWorks("{\"var\":\"X\"}") {
            varOf("X")
        }
    }

    @Test
    fun testVariablesDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, deserializer.mimeType)

        val actual = """---
                |var: "X"
                |
                """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            varOf("X")
        }
    }

    @Test
    fun testConstantDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("true") {
            truthOf(true)
        }
        deserializer.assertTermDeserializationWorks("false") {
            truthOf(false)
        }
    }

    @Test
    fun testConstantDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("--- true\n") {
            truthOf(true)
        }
        deserializer.assertTermDeserializationWorks("--- false\n") {
            truthOf(false)
        }
    }
}