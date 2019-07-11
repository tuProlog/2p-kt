package it.unibo.tuprolog.unify

import it.unibo.tuprolog.unify.testutils.EquationUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Equation] and its internal classes
 *
 * @author Enrico
 */
internal class EquationTest {

    @Test
    fun identityLhsAndRhsCorrect() {
        val toBeTested = EquationUtils.identityEquations.map { (lhs, rhs) -> Equation.Identity(lhs, rhs) }

        assertEquals(EquationUtils.identityEquations, toBeTested.map { (lhs, rhs) -> Pair(lhs, rhs) })
    }

    @Test
    fun assignmentLhsAndRhsCorrect() {
        val toBeTested = EquationUtils.assignmentEquations.map { (lhs, rhs) -> Equation.Assignment(lhs, rhs) }

        assertEquals(EquationUtils.assignmentEquations, toBeTested.map { (lhs, rhs) -> Pair(lhs, rhs) })
    }
}
