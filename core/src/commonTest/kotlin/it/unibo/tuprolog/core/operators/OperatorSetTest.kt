package it.unibo.tuprolog.core.operators

import it.unibo.tuprolog.core.operators.testutils.OperatorSetUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [OperatorSet]
 *
 * @author Enrico
 */
internal class OperatorSetTest {
    private val fakePlusOperator = Operator("+", Specifier.YFX, 500)
    private val fakeMinusOperator = Operator("-", Specifier.YFX, 500)
    private val fakeTimesOperator = Operator("*", Specifier.YFX, 400)
    private val fakeDivisionOperator = Operator("/", Specifier.YFX, 400)

    private val operatorSet = OperatorSet(fakePlusOperator, fakeMinusOperator, fakeTimesOperator, fakeDivisionOperator)

    private val overridingFakePlusOperator = Operator("+", Specifier.YFX, 1000)
    private val overridingOperatorSet = OperatorSet(overridingFakePlusOperator)

    @Test
    fun constructingOperatorSetWithEqualsOperatorsTakesOnlyFirst() {
        val toBeTested = OperatorSet(fakePlusOperator, overridingFakePlusOperator)

        assertTrue { toBeTested.any { it === fakePlusOperator } }
        assertTrue { toBeTested.none { it === overridingFakePlusOperator } }
    }

    @Test
    fun plusOperator() {
        val toBeTested = operatorSet + overridingFakePlusOperator

        assertTrue { toBeTested.any { it === overridingFakePlusOperator } }
        assertTrue { toBeTested.none { it === fakePlusOperator } }
    }

    @Test
    fun plusOperatorSet() {
        val toBeTested = operatorSet + overridingOperatorSet

        assertTrue { toBeTested.any { it === overridingFakePlusOperator } }
        assertTrue { toBeTested.none { it === fakePlusOperator } }
    }

    @Test
    fun minusOperator() {
        val toBeTested = operatorSet - fakePlusOperator

        assertTrue { toBeTested.none { it === fakePlusOperator } }
        assertTrue { toBeTested.none { it === overridingFakePlusOperator } }
    }

    @Test
    fun minusOperatorSet() {
        val toBeTested = operatorSet - OperatorSet(fakePlusOperator)

        assertTrue { toBeTested.none { it === fakePlusOperator } }
        assertTrue { toBeTested.none { it === overridingFakePlusOperator } }
    }

    @Test
    fun sameContentsOperatorSetsAreEquals() {
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

    @Test
    fun predefinedOperatorsCorrect() {
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
}
