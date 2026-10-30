package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.BaseLogicProgrammingScope
import it.unibo.tuprolog.dsl.LogicProgrammingScopeTester
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogicProgrammingScopeWithTheoriesTester<S : BaseLogicProgrammingScope<*>>(
    createLogicProgrammingScope: () -> S,
) : LogicProgrammingScopeTester<S>(createLogicProgrammingScope) {
    fun LogicProgrammingScopeWithTheories<*>.theoryAssertions(theory: Theory) {
        assertEquals(3, theory.size)
        assertTrue(factOf(structOf("f", varOf("X"))) in theory)
        assertTrue(ruleOf(structOf("f", varOf("X")), atomOf("a")) in theory)
        assertTrue(directiveOf(structOf("f", varOf("X"))) in theory)
        val rules = theory.rules.toList()
        assertEquals(2, rules.size)
        assertStructurallyEquals(rules[0].head, rules[1].head)
        assertStructurallyEquals(
            rules[0].head,
            theory.directives
                .single()
                .bodyItems
                .single(),
        )
    }
}
