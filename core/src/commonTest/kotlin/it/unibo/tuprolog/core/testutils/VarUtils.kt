package it.unibo.tuprolog.core.testutils

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals

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
        actual.name shouldBe expected.name
        actual.completeName shouldNotBe expected.completeName

        assertStructurallyEquals(expected, actual)
        actual shouldNotBe expected
    }

    /** Asserts that the variables are tied with each other, so are the same */
    internal fun assertSameVariable(
        expected: Var,
        actual: Var,
    ) {
        actual.name shouldBe expected.name
        actual.completeName shouldBe expected.completeName

        assertEqualities(expected, actual)
        actual shouldBe expected
    }
}
