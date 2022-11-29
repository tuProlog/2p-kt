package it.unibo.tuprolog.serialize

import kotlin.test.Test

class TestTheoryDeserialization {

    @Test
    fun testTheorySerializationJSON() {
        TheoryDeserializer.of(MimeType.Json).assertDeserializationWorks(Instances.commonRules, Instances.commonRulesInJSON)
    }

    @Test
    fun testTheorySerializationYAML() {
        TheoryDeserializer.of(MimeType.Yaml).assertDeserializationWorks(Instances.commonRules, Instances.commonRulesInYAML)
    }
}
