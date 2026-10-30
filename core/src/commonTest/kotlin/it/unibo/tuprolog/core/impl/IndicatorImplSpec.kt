package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IndicatorUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [IndicatorImpl] and [Indicator]
 *
 * @author Enrico
 */
class IndicatorImplSpec :
    FunSpec({
        val indicatorInstances = IndicatorUtils.mixedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

        val wellFormedIndicatorInstances =
            IndicatorUtils.wellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

        test("name-term-correct") {
            val correctNameTerms = IndicatorUtils.mixedIndicators.map { (name, _) -> name }

            onCorrespondingItems(correctNameTerms, indicatorInstances.map { it.nameTerm }, ::assertEqualities)
        }

        test("arity-term-correct") {
            val correctArityTerms = IndicatorUtils.mixedIndicators.map { (_, arity) -> arity }

            onCorrespondingItems(correctArityTerms, indicatorInstances.map { it.arityTerm }, ::assertEqualities)
        }

        test("indicator-functor") {
            indicatorInstances.forEach { assertEquals("/", it.functor) }
        }

        test("arg-correct") {
            val correctArgs = IndicatorUtils.mixedIndicators.map { (name, arity) -> listOf(name, arity) }

            onCorrespondingItems(correctArgs, indicatorInstances.map { it.args }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("indicated-name-correct") {
            val correctIndicatedName = IndicatorUtils.rawWellFormedIndicators.map { (name, _) -> name }

            onCorrespondingItems(
                correctIndicatedName,
                wellFormedIndicatorInstances.map { it.indicatedName },
            ) { expected, actual -> assertEquals(expected, actual) }
        }

        test("indicated-name-null-if-indicator-name-not-well-formed") {
            val correct = arrayOfNulls<String?>(IndicatorUtils.nonWellFormedNameIndicator.count()).toList()
            val toBeTested =
                IndicatorUtils.nonWellFormedNameIndicator
                    .map { (name, arity) -> IndicatorImpl(name, arity) }
                    .map { it.indicatedName }

            onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("indicated-arity-correct") {
            val correctIndicatedArity = IndicatorUtils.rawWellFormedIndicators.map { (_, arity) -> arity }

            onCorrespondingItems(
                correctIndicatedArity,
                wellFormedIndicatorInstances.map { it.indicatedArity },
            ) { expected, actual -> assertEquals(expected, actual) }
        }

        test("indicated-arity-null-if-indicator-arity-not-well-formed") {
            val correct = arrayOfNulls<Int?>(IndicatorUtils.nonWellFormedArityIndicator.count()).toList()
            val toBeTested =
                IndicatorUtils.nonWellFormedArityIndicator
                    .map { (name, arity) -> IndicatorImpl(name, arity) }
                    .map { it.indicatedArity }

            onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("to-string-works-as-expected") {
            val correctToString =
                IndicatorUtils.mixedIndicators.map { (name, arity) ->
                    "$name${Indicator.FUNCTOR}$arity"
                }

            onCorrespondingItems(
                correctToString,
                indicatorInstances.map { it.toString() },
            ) { expected, actual -> assertEquals(expected, actual) }
        }

        test("is-properties-and-types") {
            indicatorInstances.forEach(TermTypeAssertionUtils::assertIsIndicator)
        }

        test("two-arity") {
            indicatorInstances.forEach { assertEquals(2, it.arity) }
        }

        test("is-well-formed-works-as-expected") {
            val wellFormedInstances =
                IndicatorUtils.wellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }
            val nonWellFormedInstances =
                IndicatorUtils.nonWellFormedIndicators.map { (name, arity) -> IndicatorImpl(name, arity) }

            wellFormedInstances.forEach { assertTrue { it.isWellFormed } }
            nonWellFormedInstances.forEach { assertFalse { it.isWellFormed } }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            indicatorInstances.filter { it.isGround }.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            indicatorInstances.filterNot { it.isGround }.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }

        test("fresh-copy-merges-different-variables-with-same-name") {
            StructUtils.assertFreshCopyMergesDifferentVariablesWithSameName(::IndicatorImpl)
        }
    })
