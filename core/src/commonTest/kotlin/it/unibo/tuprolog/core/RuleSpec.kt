package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.RuleImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.RuleUtils

/**
 * Test class for [Rule] companion object
 *
 * @author Enrico
 */
class RuleSpec :
    FunSpec({
        val correctInstances = RuleUtils.mixedRules.map { (head, body) -> RuleImpl(head, body) }

        test("rule-of-with-only-head-provided-returns-a-fact") {
            val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
            val toBeTested = FactUtils.mixedFacts.map { Rule.of(it) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("rule-of-with-head-and-true-body-provided-returns-a-fact") {
            val correctInstances = FactUtils.mixedFacts.map { Fact.of(it) }
            val toBeTested = FactUtils.mixedFacts.map { Rule.of(it, Truth.TRUE) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("rule-of-with-other-arguments-returns-correct-rule") {
            val toBeTested = RuleUtils.mixedRules.map { (head, body) -> Rule.of(head, body) }

            onCorrespondingItems(correctInstances, toBeTested, ::assertEqualities)
        }

        test("rule-of-passing-more-terms-creates-body-as-tuple") {
            val bodyVariables = arrayOf(Var.of("A"), Var.of("B"))

            val correctInstance = RuleImpl(Atom.of("a"), Tuple.wrapIfNeeded(*bodyVariables))
            val toBeTested = Rule.of(Atom.of("a"), *bodyVariables)

            assertEqualities(correctInstance, toBeTested)
        }
    })
