package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IndicatorUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [IndicatorImpl] and [Indicator]
 *
 * @author Enrico
 */
internal class IndicatorImplTest {

    private val indicatorInstances = IndicatorUtils.mixedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

    @Test
    fun indicatedNameCorrect() {
        val correctIndicatedNames = IndicatorUtils.mixedIndicators.map { (name, _) -> name }

        onCorrespondingItems(correctIndicatedNames, indicatorInstances.map { it.indicatedName }, ::assertEqualities)
    }

    @Test
    fun indicatedArityCorrect() {
        val correctIndicatedArity = IndicatorUtils.mixedIndicators.map { (_, arity) -> arity }

        onCorrespondingItems(correctIndicatedArity, indicatorInstances.map { it.indicatedArity }, ::assertEqualities)
    }

    @Test
    fun indicatorFunctor() {
        indicatorInstances.forEach { assertEquals("/", it.functor) }
    }

    @Test
    fun argCorrect() {
        val correctArgs = IndicatorUtils.mixedIndicators.map { (name, arity) -> arrayOf(name, arity) }

        onCorrespondingItems(correctArgs, indicatorInstances.map { it.args }) { expected, actual ->
            assertEquals(expected.toList(), actual.toList())
            assertTrue { expected.contentDeepEquals(actual) }
        }
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToString = IndicatorUtils.mixedIndicators.map { (name, arity) -> "$name${Indicator.FUNCTOR}$arity" }

        onCorrespondingItems(correctToString, indicatorInstances.map { it.toString() }) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        indicatorInstances.forEach(TermTypeAssertionUtils::assertIsIndicator)
    }

    @Test
    fun twoArity() {
        indicatorInstances.forEach { assertEquals(2, it.arity) }
    }

    @Test
    fun isWellFormedWorksAsExpected() {
        val wellFormedInstances = IndicatorUtils.wellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }
        val nonWellFormedInstances = IndicatorUtils.nonWellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

        wellFormedInstances.forEach { assertTrue { it.isWellFormed } }
        nonWellFormedInstances.forEach { assertFalse { it.isWellFormed } }
    }

    @Test
    fun freshCopyShouldReturnInstanceItselfIfGround() {
        indicatorInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun freshCopyShouldRenewVariables() {
        indicatorInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
    }

    @Test
    fun freshCopyMergesDifferentVariablesWithSameName() {
        StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::IndicatorImpl)
    }

}
