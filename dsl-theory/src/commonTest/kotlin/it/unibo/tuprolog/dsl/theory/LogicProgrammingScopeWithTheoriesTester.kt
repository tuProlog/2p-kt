package it.unibo.tuprolog.dsl.theory

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.dsl.BaseLogicProgrammingScope
import it.unibo.tuprolog.dsl.LogicProgrammingScopeTester
import it.unibo.tuprolog.theory.Theory

class LogicProgrammingScopeWithTheoriesTester<S : BaseLogicProgrammingScope<*>>(
    createLogicProgrammingScope: () -> S,
) : LogicProgrammingScopeTester<S>(createLogicProgrammingScope) {
    fun LogicProgrammingScopeWithTheories<*>.theoryAssertions(theory: Theory) {
        theory.size shouldBe 3
        (factOf(structOf("f", varOf("X"))) in theory).shouldBeTrue()
        (ruleOf(structOf("f", varOf("X")), atomOf("a")) in theory).shouldBeTrue()
        (directiveOf(structOf("f", varOf("X"))) in theory).shouldBeTrue()
        val rules = theory.rules.toList()
        rules.size shouldBe 2
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
