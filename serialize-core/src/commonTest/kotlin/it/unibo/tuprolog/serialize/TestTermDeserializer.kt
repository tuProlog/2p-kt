package it.unibo.tuprolog.serialize

import kotlin.test.Test
import kotlin.test.assertEquals

class TestTermDeserializer {
    @Test
    fun testTailedListSerializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)

        deserializer.assertTermDeserializationWorks(
            "{\"fun\":\"member\",\"args\":[{\"var\":\"H\"},{\"list\":[{\"var\":\"H\"}],\"tail\":{\"var\":\"_\"}}]}",
        ) {
            structOf("member", varOf("H"), consOf(varOf("H"), anonymous()))
        }
    }

    @Test
    fun testTailedListSerializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)

        deserializer.assertTermDeserializationWorks(
            """
            |fun: member
            |args:
            |  - var: H
            |  - list: 
            |      - var: H
            |    tail: 
            |      var: _
            """.trimMargin(),
        ) {
            structOf("member", varOf("H"), consOf(varOf("H"), anonymous()))
        }
    }

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
        deserializer.assertTermDeserializationWorks("\"hello\"") {
            atomOf("hello")
        }
        deserializer.assertTermDeserializationWorks("\"other atom\"") {
            atomOf("other atom")
        }
    }

    @Test
    fun testNumericDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("-3") {
            intOf("-3")
        }
        deserializer.assertTermDeserializationWorks("3.1") {
            realOf("3.1")
        }
    }

    @Test
    fun testNumericDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("2") {
            intOf("2")
        }
        deserializer.assertTermDeserializationWorks("3.1") {
            realOf("3.1")
        }
    }

    @Test
    fun testListDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("{\"list\":[\"hello\",1,true]}") {
            logicListOf(atomOf("hello"), numOf(1), truthOf(true))
        }
    }

    @Test
    fun testListDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        val actual =
            """
                |list:
                |- hello
                |- 1
                |- true
            """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            logicListOf(atomOf("hello"), numOf(1), truthOf(true))
        }
    }

    @Test
    fun testBlockDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("{\"block\":[\"hello\",1, false]}") {
            blockOf(atomOf("hello"), numOf(1), truthOf(false))
        }
    }

    @Test
    fun testBlockDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        val actual =
            """
                |block:
                |- "hello"
                |- 1
                |- true
                |
            """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            blockOf(atomOf("hello"), numOf(1), truthOf(true))
        }
    }

    @Test
    fun testStructDeserializationInJSON() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
        assertEquals(MimeType.Json, deserializer.mimeType)
        deserializer.assertTermDeserializationWorks("{\"fun\":\"f\",\"args\":[\"hello\",2]}") {
            structOf("f", atomOf("hello"), numOf(2))
        }

        deserializer.assertTermDeserializationWorks(
            "{\"fun\":\"f\",\"args\":[\"prova 2\",{\"real\":3.0},{\"list\":[\"qua ci va una lista\",true]}]}",
        ) {
            structOf(
                "f",
                atomOf("prova 2"),
                realOf(3.0),
                logicListOf(atomOf("qua ci va una lista"), truthOf(true)),
            )
        }
    }

    @Test
    fun testStructDeserializationInYAML() {
        val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, deserializer.mimeType)
        var actual =
            """
                |fun: f
                |args:
                |- hello
                |- 2
            """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            structOf("f", atomOf("hello"), numOf(2))
        }

        actual =
            """
                |fun: "f"
                |args:
                |- "prova 2"
                |- real: 3.0
                |- list:
                |  - "qua ci va una lista"
                |  - true
            """.trimMargin()
        deserializer.assertTermDeserializationWorks(actual) {
            structOf(
                "f",
                atomOf("prova 2"),
                realOf(3.0),
                logicListOf(atomOf("qua ci va una lista"), truthOf(true)),
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

        val actual =
            """
                |var: "X"
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
        deserializer.assertTermDeserializationWorks("true") {
            truthOf(true)
        }
        deserializer.assertTermDeserializationWorks("false") {
            truthOf(false)
        }
    }
}
