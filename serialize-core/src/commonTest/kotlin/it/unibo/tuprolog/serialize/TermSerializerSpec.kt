package it.unibo.tuprolog.serialize

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class TermSerializerSpec :
    FunSpec({
        test("atom-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("\"atom\"") {
                atomOf("atom")
            }

            serializer.assertTermSerializationWorks("\"an atom\"") {
                atomOf("an atom")
            }
        }

        test("atom-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            assertEquals(MimeType.Yaml, serializer.mimeType)

            serializer.assertTermSerializationWorks("\"atom\"") {
                atomOf("atom")
            }
            serializer.assertTermSerializationWorks("\"an atom\"") {
                atomOf("an atom")
            }
        }

        test("numeric-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("2") {
                numOf(2)
            }

            serializer.assertTermSerializationWorks("{\"real\": \"3.0\"}") {
                numOf("3.0")
            }
        }

        test("numeric-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            assertEquals(MimeType.Yaml, serializer.mimeType)

            serializer.assertTermSerializationWorks("3") {
                numOf(3)
            }
            serializer.assertTermSerializationWorks("real: \"4.2\"") {
                numOf("4.2")
            }
        }

        test("list-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("{\"list\":[\"hello\",false]}") {
                logicListOf(atomOf("hello"), truthOf(false))
            }

            serializer.assertTermSerializationWorks("{\"list\":[\"hello\",1]}") {
                logicListOf(atomOf("hello"), numOf(1))
            }
        }

        test("tailed-list-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks(
                "{\"fun\":\"member\",\"args\":[{\"var\":\"H\"},{\"list\":[{\"var\":\"H\"}],\"tail\":{\"var\":\"_\"}}]}",
            ) {
                structOf("member", varOf("H"), consOf(varOf("H"), anonymous()))
            }
        }

        test("list-serialization-in-yaml") {
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

        test("tailed-list-serialization-in-yaml") {
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

        test("block-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("{\"block\":[\"hello\",1]}") {
                blockOf(atomOf("hello"), numOf(1))
            }
        }

        test("block-serialization-in-yaml") {
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

        test("struct-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("{\"fun\":\"f\",\"args\":[\"hello\",2]}") {
                structOf("f", atomOf("hello"), numOf(2))
            }
        }

        test("struct-serialization-in-yaml") {
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

        test("constant-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("true") {
                truthOf(true)
            }

            serializer.assertTermSerializationWorks("false") {
                truthOf(false)
            }
        }

        test("constant-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            assertEquals(MimeType.Yaml, serializer.mimeType)

            serializer.assertTermSerializationWorks("true") {
                truthOf(true)
            }
            serializer.assertTermSerializationWorks("false") {
                truthOf(false)
            }
        }

        test("variables-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            assertEquals(MimeType.Json, serializer.mimeType)

            serializer.assertTermSerializationWorks("{\"var\":\"Y\"}") {
                varOf("Y")
            }

            serializer.assertTermSerializationWorks("{\"var\":\"Two Words\"}") {
                varOf("Two Words")
            }
        }

        test("variables-serialization-in-yaml") {
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
    })
