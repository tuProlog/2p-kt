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
    private val wellFormedIndicatorInstances =
        IndicatorUtils.wellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

    @Test
    fun nameTermCorrect() {
        val correctNameTerms = IndicatorUtils.mixedIndicators.map { (name, _) -> name }

        onCorrespondingItems(correctNameTerms, indicatorInstances.map { it.nameTerm }, ::assertEqualities)
    }

    @Test
    fun arityTermCorrect() {
        val correctArityTerms = IndicatorUtils.mixedIndicators.map { (_, arity) -> arity }

        onCorrespondingItems(correctArityTerms, indicatorInstances.map { it.arityTerm }, ::assertEqualities)
    }

    @Test
    fun indicatorFunctor() {
        indicatorInstances.forEach { assertEquals("/", it.functor) }
    }

    @Test
    fun argCorrect() {
        val correctArgs = IndicatorUtils.mixedIndicators.map { (name, arity) -> listOf(name, arity) }

        onCorrespondingItems(correctArgs, indicatorInstances.map { it.args }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun indicatedNameCorrect() {
        val correctIndicatedName = IndicatorUtils.rawWellFormedIndicators.map { (name, _) -> name }

        onCorrespondingItems(
            correctIndicatedName,
            wellFormedIndicatorInstances.map { it.indicatedName },
        ) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun indicatedNameNullIfIndicatorNameNotWellFormed() {
        val correct = arrayOfNulls<String?>(IndicatorUtils.nonWellFormedNameIndicator.count()).toList()
        val toBeTested =
            IndicatorUtils.nonWellFormedNameIndicator
                .map { (name, arity) -> IndicatorImpl(name, arity) }
                .map { it.indicatedName }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun indicatedArityCorrect() {
        val correctIndicatedArity = IndicatorUtils.rawWellFormedIndicators.map { (_, arity) -> arity }

        onCorrespondingItems(
            correctIndicatedArity,
            wellFormedIndicatorInstances.map { it.indicatedArity },
        ) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun indicatedArityNullIfIndicatorArityNotWellFormed() {
        val correct = arrayOfNulls<Int?>(IndicatorUtils.nonWellFormedArityIndicator.count()).toList()
        val toBeTested =
            IndicatorUtils.nonWellFormedArityIndicator
                .map { (name, arity) -> IndicatorImpl(name, arity) }
                .map { it.indicatedArity }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctToString = IndicatorUtils.mixedIndicators.map { (name, arity) -> "$name${Indicator.FUNCTOR}$arity" }

        onCorrespondingItems(
            correctToString,
            indicatorInstances.map { it.toString() },
        ) { expected, actual -> assertEquals(expected, actual) }
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
        val wellFormedInstances =
            IndicatorUtils.wellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }
        val nonWellFormedInstances =
            IndicatorUtils.nonWellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

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
