package it.unibo.tuprolog.core.operators

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.operators.testutils.OperatorSetUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [OperatorSet]
 *
 * @author Enrico
 */
class OperatorSetSpec :
    FunSpec({
        val fakePlusOperator = Operator("+", Specifier.YFX, 500)

        val fakeMinusOperator = Operator("-", Specifier.YFX, 500)

        val fakeTimesOperator = Operator("*", Specifier.YFX, 400)

        val fakeDivisionOperator = Operator("/", Specifier.YFX, 400)

        val operatorSet = OperatorSet(fakePlusOperator, fakeMinusOperator, fakeTimesOperator, fakeDivisionOperator)

        val overridingFakePlusOperator = Operator("+", Specifier.YFX, 1000)

        val overridingOperatorSet = OperatorSet(overridingFakePlusOperator)

        test("constructing-operator-set-with-equals-operators-takes-only-first") {
            val toBeTested = OperatorSet(fakePlusOperator, overridingFakePlusOperator)

            assertTrue { toBeTested.any { it === fakePlusOperator } }
            assertTrue { toBeTested.none { it === overridingFakePlusOperator } }
        }

        test("plus-operator") {
            val toBeTested = operatorSet + overridingFakePlusOperator

            assertTrue { toBeTested.any { it === overridingFakePlusOperator } }
            assertTrue { toBeTested.none { it === fakePlusOperator } }
        }

        test("plus-operator-set") {
            val toBeTested = operatorSet + overridingOperatorSet

            assertTrue { toBeTested.any { it === overridingFakePlusOperator } }
            assertTrue { toBeTested.none { it === fakePlusOperator } }
        }

        test("minus-operator") {
            val toBeTested = operatorSet - fakePlusOperator

            assertTrue { toBeTested.none { it === fakePlusOperator } }
            assertTrue { toBeTested.none { it === overridingFakePlusOperator } }
        }

        test("minus-operator-set") {
            val toBeTested = operatorSet - OperatorSet(fakePlusOperator)

            assertTrue { toBeTested.none { it === fakePlusOperator } }
            assertTrue { toBeTested.none { it === overridingFakePlusOperator } }
        }

        test("same-contents-operator-sets-are-equals") {
            assertEquals(OperatorSet(), OperatorSet())
            assertEquals(
                operatorSet,
                OperatorSet(fakePlusOperator, fakeMinusOperator, fakeTimesOperator, fakeDivisionOperator),
            )
            assertEquals(
                operatorSet,
                OperatorSet(fakeMinusOperator, fakePlusOperator, fakeDivisionOperator, fakeTimesOperator),
            )

            assertNotEquals(operatorSet, OperatorSet())
        }

        test("predefined-operators-correct") {
            assertEquals(OperatorSet.DEFAULT.count(), OperatorSetUtils.defaultOperators.count())

            OperatorSetUtils.defaultOperators.forEach { expectedTriple ->
                val (expectedFunctor, expectedSpecifier, expectedPriority) = expectedTriple

                // expectedOperator should be present in OperatorSet
                val actualOperator =
                    OperatorSet.DEFAULT.single { it == Operator(expectedFunctor, expectedSpecifier, expectedPriority) }

                assertEquals(
                    expectedFunctor,
                    actualOperator.functor,
                    "$actualOperator functor expected to be: $expectedFunctor",
                )
                assertEquals(
                    expectedSpecifier,
                    actualOperator.specifier,
                    "$actualOperator specifier expected to be: $expectedSpecifier",
                )
                assertEquals(
                    expectedPriority,
                    actualOperator.priority,
                    "$actualOperator priority expected to be: $expectedPriority",
                )
            }
        }
    })
