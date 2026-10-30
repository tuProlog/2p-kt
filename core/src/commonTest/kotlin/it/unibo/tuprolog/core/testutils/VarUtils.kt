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
    /** A named variable name, for parameterized tests */
    internal data class VarCase(
        val id: String,
        val name: String,
    )

    /** Contains variables that are correctly named */
    val correctlyNamedVarCases by lazy {
        listOf(
            VarCase("uppercase-a", "A"),
            VarCase("uppercase-x", "X"),
            VarCase("anonymous", "_"),
            VarCase("underscore-initial", "_X"),
            VarCase("underscore-digit", "_1"),
            VarCase("uppercase-digit", "X1"),
            VarCase("uppercase-underscore-digit", "X_1"),
        )
    }

    /** Contains variables that aren't correctly named */
    val incorrectlyNamedVarCases by lazy {
        listOf(
            VarCase("lowercase-a", "a"),
            VarCase("lowercase-x", "x"),
        )
    }

    /** Contains mixed variables, correctly and incorrectly named */
    val mixedVarCases by lazy { correctlyNamedVarCases + incorrectlyNamedVarCases }

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
