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

        serializer.assertTermSerializationWorks("\"atom\"") {
            atomOf("atom")
        }
        serializer.assertTermSerializationWorks("\"an atom\"") {
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

        serializer.assertTermSerializationWorks("{\"real\": \"3.0\"}") {
            numOf("3.0")
        }
    }

    @Test
    fun testNumericSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        serializer.assertTermSerializationWorks("3") {
            numOf(3)
        }
        serializer.assertTermSerializationWorks("real: \"4.2\"") {
            numOf("4.2")
        }
    }

    @Test
    fun testListSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("{\"list\":[\"hello\",false]}") {
            logicListOf(atomOf("hello"), truthOf(false))
        }

        serializer.assertTermSerializationWorks("{\"list\":[\"hello\",1]}") {
            logicListOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testTailedListSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks(
            "{\"fun\":\"member\",\"args\":[{\"var\":\"H\"},{\"list\":[{\"var\":\"H\"}],\"tail\":{\"var\":\"_\"}}]}",
        ) {
            structOf("member", varOf("H"), consOf(varOf("H"), anonymous()))
        }
    }

    @Test
    fun testListSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        var expected =
            """
                |list:
                |- hello
                |- false
            """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            logicListOf(atomOf("hello"), truthOf(false))
        }

        expected =
            """
                |list:
                |- hello
                |- 1
            """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            logicListOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testTailedListSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, serializer.mimeType)

        serializer.assertTermSerializationWorks(
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
    fun testBlockSerializationInJSON() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
        assertEquals(MimeType.Json, serializer.mimeType)

        serializer.assertTermSerializationWorks("{\"block\":[\"hello\",1]}") {
            blockOf(atomOf("hello"), numOf(1))
        }
    }

    @Test
    fun testBlockSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

        assertEquals(MimeType.Yaml, serializer.mimeType)

        var expected =
            """
                |block:
                |- hello
                |- false
            """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            blockOf(atomOf("hello"), truthOf(false))
        }

        expected =
            """
                |block:
                |- hello
                |- 1
            """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            blockOf(atomOf("hello"), numOf(1))
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

        val expected =
            """
                |fun: f
                |args:
                |- hello
                |- 2
                |- list:
                |  - true
                |  - ciao
            """.trimMargin()
        serializer.assertTermSerializationWorks(expected) {
            structOf("f", atomOf("hello"), numOf(2), logicListOf(truthOf(true), atomOf("ciao")))
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

        serializer.assertTermSerializationWorks("true") {
            truthOf(true)
        }
        serializer.assertTermSerializationWorks("false") {
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

        serializer.assertTermSerializationWorks("{\"var\":\"Two Words\"}") {
            varOf("Two Words")
        }
    }

    @Test
    fun testVariablesSerializationInYAML() {
        val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)
        assertEquals(MimeType.Yaml, serializer.mimeType)

        val expected =
            """
                |var: "Y"
            """.trimMargin()

        serializer.assertTermSerializationWorks(expected) {
            varOf("Y")
        }
    }
}
