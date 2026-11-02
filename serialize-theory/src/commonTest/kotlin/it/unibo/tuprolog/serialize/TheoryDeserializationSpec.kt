package it.unibo.tuprolog.serialize

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import it.unibo.tuprolog.serialize.ObjectsUtils.parseAsObject

private val casesByMimeType = theoryCases.flatMap { case -> textualMimeTypes.map { case to it } }

private val nonClauses = listOf("atom" to "\"a\"", "number" to "1", "variable" to "{\"var\":\"X\"}")

class TheoryDeserializationSpec :
    FunSpec({
        withTests(nameFn = { (case, mimeType) ->
            "deserialize/${case.id}/${mimeType.subType}"
        }, casesByMimeType) { (case, mimeType) ->
            TheoryDeserializer.of(mimeType).assertDeserializationWorks(case.theory(), case.text(mimeType))
        }

        withTests(
            nameFn = { (case, mimeType) -> "round-trip/${case.id}/${mimeType.subType}" },
            casesByMimeType,
        ) { (case, mimeType) ->
            val theory = case.theory()
            TheoryDeserializer
                .of(
                    mimeType,
                ).assertDeserializationWorks(theory, TheorySerializer.of(mimeType).serialize(theory))
        }

        withTests(nameFn = { "deserialize-many/${it.subType}" }, textualMimeTypes) { mimeType ->
            val theories = theoryCases.map { it.theory() }
            val deserialized =
                TheoryDeserializer.of(mimeType).deserializeMany(TheorySerializer.of(mimeType).serializeMany(theories))
            deserialized.toList() shouldHaveSize theories.size
            theories.zip(deserialized).forEach { (expected, actual) ->
                representationsAreEqual(expected, actual).shouldBeTrue()
            }
        }

        withTests(nameFn = { "deobjectify/${it.id}" }, theoryCases) { case ->
            val theory = TheoryDeobjectifier.default.deobjectify(parseAsObject(case.json, MimeType.Json))
            representationsAreEqual(case.theory(), theory).shouldBeTrue()
        }

        withTests(nameFn = { "non-clause-is-malformed-input/${it.first}" }, nonClauses) { (_, element) ->
            shouldThrow<DeobjectificationException> {
                TheoryDeserializer
                    .of(MimeType.Json)
                    .deserialize("[$element]")
                    .clauses
                    .toList()
            }
        }
    })
