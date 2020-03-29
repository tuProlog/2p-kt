package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RuleUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [RuleImpl] and [Rule]
 *
 * @author Enrico
 */
internal class RuleImplTest {

    private val groundRulesInstances = RuleUtils.groundRules.map { (head, body) -> RuleImpl(head, body) }
    private val nonGroundRulesInstances = RuleUtils.nonGroundRules.map { (head, body) -> RuleImpl(head, body) }
    private val wellFormedRulesInstances = RuleUtils.wellFormedRules.map { (head, body) -> RuleImpl(head, body) }
    private val nonWellFormedRulesInstances = RuleUtils.nonWellFormedRules.map { (head, body) -> RuleImpl(head, body) }
    private val mixedRulesInstances = RuleUtils.mixedRules.map { (head, body) -> RuleImpl(head, body) }

    @Test
    fun headCorrect() {
        val correctHeads = RuleUtils.mixedRules.map { (head, _) -> head }

        onCorrespondingItems(correctHeads, mixedRulesInstances.map { it.head }, ::assertEqualities)
    }

    @Test
    fun bodyCorrect() {
        val correctBodies = RuleUtils.mixedRules.map { (_, body) -> body }

        onCorrespondingItems(correctBodies, mixedRulesInstances.map { it.body }, ::assertEqualities)
    }

    @Test
    fun isWellFormedReturnsTrueIfRuleWellFormed() {
        wellFormedRulesInstances.forEach { assertTrue("$it isWellFormed should be true") { it.isWellFormed } }
    }

    @Test
    fun isWellFormedReturnsFalseIfRuleNotWellFormed() {
        nonWellFormedRulesInstances.forEach { assertFalse("$it isWellFormed should be false") { it.isWellFormed } }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedRulesInstances.forEach(TermTypeAssertionUtils::assertIsRule)
    }

    @Test
    fun factDetected() {
        assertTrue(RuleImpl(Atom.of("hey"), Truth.TRUE).isFact)
    }

    @Test
    fun isGroundTrueIfNoVariablesArePresent() {
        groundRulesInstances.forEach { assertTrue { it.isGround } }
        nonGroundRulesInstances.forEach { assertFalse { it.isGround } }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        groundRulesInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        nonGroundRulesInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun functorCorrect() {
        mixedRulesInstances.forEach { assertEquals(":-", it.functor) }
    }

    @Test
    fun argsCorrect() {
        val correctArgs = RuleUtils.mixedRules.map { (head, body) -> arrayOf(head, body) }

        onCorrespondingItems(correctArgs, mixedRulesInstances.map { it.args }) { expected, actual ->
            assertEquals(expected.toList(), actual.toList())
            assertTrue { expected.contentDeepEquals(actual) }
        }
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToStrings = mixedRulesInstances.map { "${it.head} ${it.functor} ${it.body}" }

        onCorrespondingItems(correctToStrings, mixedRulesInstances.map { it.toString() }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }
}
