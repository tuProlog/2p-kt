package it.unibo.tuprolog.serialize

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests

private val nonClauses = listOf("atom" to "\"a\"", "number" to "1", "variable" to "{\"var\":\"X\"}")

class TheoryDeserializationSpec :
    FunSpec({
        test("theory-serialization-json") {
            TheoryDeserializer
                .of(
                    MimeType.Json,
                ).assertDeserializationWorks(Instances.commonRules, Instances.commonRulesInJSON)
        }

        test("theory-serialization-yaml") {
            TheoryDeserializer
                .of(
                    MimeType.Yaml,
                ).assertDeserializationWorks(Instances.commonRules, Instances.commonRulesInYAML)
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
