package it.unibo.tuprolog.serialize

import it.unibo.tuprolog.solve.stdlib.CommonRules
import kotlin.test.Test

class TestTheorySerialization {

    @Test
    fun testTheorySerializationJSON() {
        TheorySerializer.of(MimeType.Json).assertSerializationWorks(Instances.commonRulesInJSON, CommonRules.theory)
    }

    @Test
    fun testTheorySerializationYAML() {
        TheorySerializer.of(MimeType.Yaml).assertSerializationWorks(Instances.commonRulesInYAML, CommonRules.theory)
    }
}
