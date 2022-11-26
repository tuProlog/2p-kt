package it.unibo.tuprolog.serialize

import kotlin.test.Test

class TestTheorySerialization {

    @Test
    fun testTheorySerializationJSON() {
        TheorySerializer.of(MimeType.Json).assertSerializationWorks(Instances.commonRulesInJSON, Instances.commonRules)
    }

    @Test
    fun testTheorySerializationYAML() {
        TheorySerializer.of(MimeType.Yaml).assertSerializationWorks(Instances.commonRulesInYAML, Instances.commonRules)
    }
}
