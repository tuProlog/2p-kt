package it.unibo.tuprolog.serialize

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Truth

/** A [Truth] other than the three predefined ones, which no serializer knows. */
private class UnknownTruth : Truth by Truth.TRUE {
    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTruth(this)

    // Term declares equals and hashCode, so delegation would make this equal to Truth.TRUE
    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = 0
}

/** A term of no known kind, which no serializer knows. */
private class UnknownTerm : Term by Atom.of("unknown") {
    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTerm(this)
}

private val unrepresentable: List<Pair<String, () -> Term>> =
    listOf("unknown-truth" to ::UnknownTruth, "unknown-term" to ::UnknownTerm)

class TermSerializerSpec :
    FunSpec({
        test("atom-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("\"atom\"") {
                atomOf("atom")
            }

            serializer.assertTermSerializationWorks("\"an atom\"") {
                atomOf("an atom")
            }
        }

        test("atom-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            serializer.mimeType shouldBe MimeType.Yaml

            serializer.assertTermSerializationWorks("\"atom\"") {
                atomOf("atom")
            }
            serializer.assertTermSerializationWorks("\"an atom\"") {
                atomOf("an atom")
            }
        }

        test("numeric-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("2") {
                numOf(2)
            }

            serializer.assertTermSerializationWorks("{\"real\": \"3.0\"}") {
                numOf("3.0")
            }
        }

        test("numeric-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            serializer.mimeType shouldBe MimeType.Yaml

            serializer.assertTermSerializationWorks("3") {
                numOf(3)
            }
            serializer.assertTermSerializationWorks("real: \"4.2\"") {
                numOf("4.2")
            }
        }

        test("list-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("{\"list\":[\"hello\",false]}") {
                logicListOf(atomOf("hello"), truthOf(false))
            }

            serializer.assertTermSerializationWorks("{\"list\":[\"hello\",1]}") {
                logicListOf(atomOf("hello"), numOf(1))
            }
        }

        test("tailed-list-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks(
                "{\"fun\":\"member\",\"args\":[{\"var\":\"H\"},{\"list\":[{\"var\":\"H\"}],\"tail\":{\"var\":\"_\"}}]}",
            ) {
                structOf("member", varOf("H"), consOf(varOf("H"), anonymous()))
            }
        }

        test("list-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            serializer.mimeType shouldBe MimeType.Yaml

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
            serializer.mimeType shouldBe MimeType.Yaml

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
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("{\"block\":[\"hello\",1]}") {
                blockOf(atomOf("hello"), numOf(1))
            }
        }

        test("block-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            serializer.mimeType shouldBe MimeType.Yaml

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
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("{\"fun\":\"f\",\"args\":[\"hello\",2]}") {
                structOf("f", atomOf("hello"), numOf(2))
            }
        }

        test("struct-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            serializer.mimeType shouldBe MimeType.Yaml

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
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("true") {
                truthOf(true)
            }

            serializer.assertTermSerializationWorks("false") {
                truthOf(false)
            }
        }

        test("constant-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)

            serializer.mimeType shouldBe MimeType.Yaml

            serializer.assertTermSerializationWorks("true") {
                truthOf(true)
            }
            serializer.assertTermSerializationWorks("false") {
                truthOf(false)
            }
        }

        test("variables-serialization-in-json") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Json)
            serializer.mimeType shouldBe MimeType.Json

            serializer.assertTermSerializationWorks("{\"var\":\"Y\"}") {
                varOf("Y")
            }

            serializer.assertTermSerializationWorks("{\"var\":\"Two Words\"}") {
                varOf("Two Words")
            }
        }

        test("variables-serialization-in-yaml") {
            val serializer: TermSerializer = TermSerializer.of(MimeType.Yaml)
            serializer.mimeType shouldBe MimeType.Yaml

            val expected =
                """
                    |var: "Y"
                """.trimMargin()

            serializer.assertTermSerializationWorks(expected) {
                varOf("Y")
            }
        }

        withTests(
            nameFn = { (id, mimeType) -> "unrepresentable-term/$id/${mimeType.subType}" },
            unrepresentable.flatMap { (id, term) -> listOf(MimeType.Json, MimeType.Yaml).map { Triple(id, it, term) } },
        ) { (_, mimeType, term) ->
            shouldThrow<SerializationException> { TermSerializer.of(mimeType).serialize(term()) }
        }
    })
