package it.unibo.tuprolog.serialize

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class TermDeserializerSpec :
    FunSpec({
        test("tailed-list-serialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
            assertEquals(MimeType.Json, deserializer.mimeType)

            deserializer.assertTermDeserializationWorks(
                "{\"fun\":\"member\",\"args\":[{\"var\":\"H\"},{\"list\":[{\"var\":\"H\"}],\"tail\":{\"var\":\"_\"}}]}",
            ) {
                structOf("member", varOf("H"), consOf(varOf("H"), anonymous()))
            }
        }

        test("tailed-list-serialization-in-yaml") {
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

        test("atom-deserialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
            assertEquals(MimeType.Json, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("\"hello\"") {
                atomOf("hello")
            }
            deserializer.assertTermDeserializationWorks("\"other atom\"") {
                atomOf("other atom")
            }
        }

        test("atom-deserialization-in-yaml") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
            assertEquals(MimeType.Yaml, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("\"hello\"") {
                atomOf("hello")
            }
            deserializer.assertTermDeserializationWorks("\"other atom\"") {
                atomOf("other atom")
            }
        }

        test("numeric-deserialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
            assertEquals(MimeType.Json, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("-3") {
                intOf("-3")
            }
            deserializer.assertTermDeserializationWorks("3.1") {
                realOf("3.1")
            }
        }

        test("numeric-deserialization-in-yaml") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
            assertEquals(MimeType.Yaml, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("2") {
                intOf("2")
            }
            deserializer.assertTermDeserializationWorks("3.1") {
                realOf("3.1")
            }
        }

        test("list-deserialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
            assertEquals(MimeType.Json, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("{\"list\":[\"hello\",1,true]}") {
                logicListOf(atomOf("hello"), numOf(1), truthOf(true))
            }
        }

        test("list-deserialization-in-yaml") {
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

        test("block-deserialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
            assertEquals(MimeType.Json, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("{\"block\":[\"hello\",1, false]}") {
                blockOf(atomOf("hello"), numOf(1), truthOf(false))
            }
        }

        test("block-deserialization-in-yaml") {
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

        test("struct-deserialization-in-json") {
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

        test("struct-deserialization-in-yaml") {
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

        test("variables-deserialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)

            assertEquals(MimeType.Json, deserializer.mimeType)

            deserializer.assertTermDeserializationWorks("{\"var\":\"X\"}") {
                varOf("X")
            }
        }

        test("variables-deserialization-in-yaml") {
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

        test("constant-deserialization-in-json") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Json)
            assertEquals(MimeType.Json, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("true") {
                truthOf(true)
            }
            deserializer.assertTermDeserializationWorks("false") {
                truthOf(false)
            }
        }

        test("constant-deserialization-in-yaml") {
            val deserializer: TermDeserializer = TermDeserializer.of(MimeType.Yaml)
            assertEquals(MimeType.Yaml, deserializer.mimeType)
            deserializer.assertTermDeserializationWorks("true") {
                truthOf(true)
            }
            deserializer.assertTermDeserializationWorks("false") {
                truthOf(false)
            }
        }
    })
