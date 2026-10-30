package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [RuleImpl] and [Rule]
 *
 * @author Enrico
 */
class RuleImplSpec :
    FunSpec({
        val groundRulesInstances = RuleUtils.groundRules.map { (head, body) -> RuleImpl(head, body) }

        val nonGroundRulesInstances = RuleUtils.nonGroundRules.map { (head, body) -> RuleImpl(head, body) }

        val wellFormedRulesInstances = RuleUtils.wellFormedRules.map { (head, body) -> RuleImpl(head, body) }

        val nonWellFormedRulesInstances = RuleUtils.nonWellFormedRules.map { (head, body) -> RuleImpl(head, body) }

        val mixedRulesInstances = RuleUtils.mixedRules.map { (head, body) -> RuleImpl(head, body) }

        test("head-correct") {
            val correctHeads = RuleUtils.mixedRules.map { (head, _) -> head }

            onCorrespondingItems(correctHeads, mixedRulesInstances.map { it.head }, ::assertEqualities)
        }

        test("body-correct") {
            val correctBodies = RuleUtils.mixedRules.map { (_, body) -> body }

            onCorrespondingItems(correctBodies, mixedRulesInstances.map { it.body }, ::assertEqualities)
        }

        test("is-well-formed-returns-true-if-rule-well-formed") {
            wellFormedRulesInstances.forEach { assertTrue("$it isWellFormed should be true") { it.isWellFormed } }
        }

        test("is-well-formed-returns-false-if-rule-not-well-formed") {
            nonWellFormedRulesInstances.forEach { assertFalse("$it isWellFormed should be false") { it.isWellFormed } }
        }

        test("is-properties-and-types") {
            mixedRulesInstances.forEach(TermTypeAssertionUtils::assertIsRule)
        }

        test("fact-detected") {
            assertTrue(RuleImpl(Atom.of("hey"), Truth.TRUE).isFact)
        }

        test("is-ground-true-if-no-variables-are-present") {
            groundRulesInstances.forEach { assertTrue { it.isGround } }
            nonGroundRulesInstances.forEach { assertFalse { it.isGround } }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            groundRulesInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            nonGroundRulesInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }

        test("functor-correct") {
            mixedRulesInstances.forEach { assertEquals(":-", it.functor) }
        }

        test("args-correct") {
            val correctArgs = RuleUtils.mixedRules.map { (head, body) -> listOf(head, body) }

            onCorrespondingItems(correctArgs, mixedRulesInstances.map { it.args }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("to-string-works-as-expected") {
            val correctToStrings = mixedRulesInstances.map { "${it.head} ${it.functor} ${it.body}" }

            onCorrespondingItems(correctToStrings, mixedRulesInstances.map { it.toString() }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }
    })
