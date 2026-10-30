package it.unibo.tuprolog.serialize

import io.kotest.core.spec.style.FunSpec

class TheorySerializationSpec :
    FunSpec({
        test("theory-serialization-json") {
            TheorySerializer
                .of(
                    MimeType.Json,
                ).assertSerializationWorks(Instances.commonRulesInJSON, Instances.commonRules)
        }

        test("theory-serialization-yaml") {
            TheorySerializer
                .of(
                    MimeType.Yaml,
                ).assertSerializationWorks(Instances.commonRulesInYAML, Instances.commonRules)
        }
    })
