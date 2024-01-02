package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Utils singleton to test [Var]
 *
 * @author Enrico
 */
internal object VarUtils {
    /** Contains variables that are correctly named */
    val correctlyNamedVars by lazy { listOf("A", "X", "_", "_X", "_1", "X1", "X_1") }

    /** Contains variables that aren't correctly named */
    val incorrectlyNamedVars by lazy { listOf("a", "x") }

    /** Contains mixed variables, correctly and incorrectly named */
    val mixedVars by lazy { correctlyNamedVars + incorrectlyNamedVars }

    /** Asserts that the refreshed variable is not tied to the original variable, except for the name */
    internal fun assertDifferentVariableExceptForName(
        expected: Var,
        actual: Var,
    ) {
        assertEquals(expected.name, actual.name)
        assertNotEquals(expected.completeName, actual.completeName)

        assertStructurallyEquals(expected, actual)
        assertNotEquals(expected, actual)
    }

    /** Asserts that the variables are tied with each other, so are the same */
    internal fun assertSameVariable(
        expected: Var,
        actual: Var,
    ) {
        assertEquals(expected.name, actual.name)
        assertEquals(expected.completeName, actual.completeName)

        assertEqualities(expected, actual)
        assertEquals(expected, actual)
    }
}
