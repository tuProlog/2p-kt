package it.unibo.tuprolog.core.operators

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

    private val plusOperator = Operator("+", Associativity.YFX, 500)
    private val minusOperator = Operator("-", Associativity.YFX, 500)
    private val timesOperator = Operator("*", Associativity.YFX, 400)
    private val divisionOperator = Operator("/", Associativity.YFX, 400)

    private val operatorSet = OperatorSet(plusOperator, minusOperator, timesOperator, divisionOperator)

    private val overridingPlusOperator = Operator("+", Associativity.YFX, 1000)
    private val overridingOperatorSet = OperatorSet(overridingPlusOperator)

    @Test
    fun constructingOperatorSetWithEqualsOperatorsTakesOnlyFirst() {
        val toBeTested = OperatorSet(plusOperator, overridingPlusOperator)

        assertTrue { toBeTested.any { it === plusOperator } }
        assertTrue { toBeTested.none { it === overridingPlusOperator } }
    }

    @Test
    fun plusOperator() {
        val toBeTested = operatorSet + overridingPlusOperator

        assertTrue { toBeTested.any { it === overridingPlusOperator } }
        assertTrue { toBeTested.none { it === plusOperator } }
    }

    @Test
    fun plusOperatorSet() {
        val toBeTested = operatorSet + overridingOperatorSet

        assertTrue { toBeTested.any { it === overridingPlusOperator } }
        assertTrue { toBeTested.none { it === plusOperator } }
    }

    @Test
    fun minusOperator() {
        val toBeTested = operatorSet - plusOperator

        assertTrue { toBeTested.none { it === plusOperator } }
        assertTrue { toBeTested.none { it === overridingPlusOperator } }
    }

    @Test
    fun minusOperatorSet() {
        val toBeTested = operatorSet - OperatorSet(plusOperator)

        assertTrue { toBeTested.none { it === plusOperator } }
        assertTrue { toBeTested.none { it === overridingPlusOperator } }
    }

    @Test
    fun sameContentsOperatorSetsAreEquals() {
        assertEquals(OperatorSet(), OperatorSet())
        assertEquals(operatorSet, OperatorSet(plusOperator, minusOperator, timesOperator, divisionOperator))
        assertEquals(operatorSet, OperatorSet(minusOperator, plusOperator, divisionOperator, timesOperator))

        assertNotEquals(operatorSet, OperatorSet())
    }
}
