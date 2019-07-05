package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RuleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import kotlin.test.Test

/**
 * Test class for [Rule] companion object
 *
 * @author Enrico
 */
internal class RuleTest {

    private val correctInstances = RuleUtils.mixedRules.map { (head, body) -> RuleImpl(head, body) }

    @Test
    fun ruleOfWithOnlyHeadProvidedReturnsAFact() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map { Rule.of(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ruleOfWithHeadAndTrueBodyProvidedReturnsAFact() {
        val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
        val toBeTested = FactUtils.mixedFacts.map { Rule.of(it, Truth.`true`()) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ruleOfWithOtherArgumentsReturnsCorrectRule() {
        val toBeTested = RuleUtils.mixedRules.map { (head, body) -> Rule.of(head, body) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ruleOfPassingMoreTermsCreatesBodyAsTuple() {
        val bodyVariables = arrayOf(Var.of("A"), Var.of("B"))

        val correctInstance = RuleImpl(Atom.of("a"), Tuple.wrapIfNeeded(*bodyVariables))
        val toBeTested = Rule.of(Atom.of("a"), *bodyVariables)

        assertEqualities(correctInstance, toBeTested)
    }
}
