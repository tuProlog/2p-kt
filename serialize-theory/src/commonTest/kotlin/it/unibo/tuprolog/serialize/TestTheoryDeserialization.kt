package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.solve.stdlib.CommonRules
import kotlin.test.Test

class TestTheoryDeserialization {

    @Test
    fun testTheorySerializationJSON() {
        TheoryDeserializer.of(MimeType.Json).assertDeserializationWorks(CommonRules.theory, Instances.commonRulesInJSON)
    }

    @Test
    fun testTheorySerializationYAML() {
        TheoryDeserializer.of(MimeType.Yaml).assertDeserializationWorks(CommonRules.theory, Instances.commonRulesInYAML)
    }
}
