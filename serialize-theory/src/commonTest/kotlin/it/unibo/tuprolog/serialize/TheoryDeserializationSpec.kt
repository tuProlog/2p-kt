package it.unibo.tuprolog.serialize

import io.kotest.core.spec.style.FunSpec

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
    })
