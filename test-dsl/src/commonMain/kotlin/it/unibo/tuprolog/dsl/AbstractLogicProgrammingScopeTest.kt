package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

abstract class AbstractLogicProgrammingScopeTest<S : BaseLogicProgrammingScope<*>> {
    protected abstract fun createLogicProgrammingScope(): S

    protected fun <R> logicProgramming(function: S.() -> R): R = createLogicProgrammingScope().function()

    protected fun <T> assertLogicProgrammingExpressionIsCorrect(
        expected: T,
        actualCreator: S.() -> T,
    ) {
        assertEquals(expected, createLogicProgrammingScope().actualCreator())
    }

    protected fun <T> S.assertExpressionIsCorrect(
        expected: T,
        actualCreator: S.() -> T,
    ) {
        assertEquals(expected, actualCreator())
    }

    protected fun assertStructurallyEquals(
        first: Term,
        second: Term,
    ) {
        assertTrue(first.structurallyEquals(second))
        assertNotEquals(first, second)
    }

    protected fun S.assertAreDifferentUnderscores(
        first: Var,
        second: Var,
    ) {
        for (variable in sequenceOf(first, second)) {
            assertEquals("_", variable.name)
            assertTrue(variable.isAnonymous)
        }
        assertNotEquals(`_`, `_`)
    }
}
