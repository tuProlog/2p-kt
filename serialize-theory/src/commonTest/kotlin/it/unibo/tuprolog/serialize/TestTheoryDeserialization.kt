package it.unibo.tuprolog.serialize

import kotlin.test.Test
import kotlin.test.assertFailsWith

class TestTheoryDeserialization {
    @Test
    fun testTheorySerializationJSON() {
        TheoryDeserializer
            .of(
                MimeType.Json,
            ).assertDeserializationWorks(Instances.commonRules, Instances.commonRulesInJSON)
    }

    @Test
    fun testTheorySerializationYAML() {
        TheoryDeserializer
            .of(
                MimeType.Yaml,
            ).assertDeserializationWorks(Instances.commonRules, Instances.commonRulesInYAML)
    }

    @Test
    fun testNonClauseElementsAreRejectedInJSON() {
        val deserializer = TheoryDeserializer.of(MimeType.Json)
        for (nonClause in listOf("1", "{\"var\":\"X\"}", "{\"fun\":\"foo\",\"args\":[{\"var\":\"X\"}]}")) {
            assertFailsWith<DeobjectificationException>(nonClause) {
                deserializer.deserialize("[$nonClause]")
            }
        }
    }

    @Test
    fun testNonClauseElementsAreRejectedInYAML() {
        val deserializer = TheoryDeserializer.of(MimeType.Yaml)
        for (nonClause in listOf("1", "var: \"X\"", "fun: \"foo\"\n  args:\n  - var: \"X\"")) {
            assertFailsWith<DeobjectificationException>(nonClause) {
                deserializer.deserialize("- $nonClause")
            }
        }
    }
}
