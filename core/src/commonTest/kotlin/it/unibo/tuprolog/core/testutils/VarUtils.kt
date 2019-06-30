package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Var

/**
 * Utils singleton to test [Var]
 *
 * @author Enrico
 */
internal object VarUtils {

    /**
     * Contains variables that are correctly named
     */
    val correctlyNamedVars by lazy { listOf("A", "X", "_", "_X", "_1", "X1", "X_1") }

    /**
     * Contains variables that aren't correctly named
     */
    val incorrectlyNamedVars by lazy { listOf("a", "x") }

    /**
     * Contains mixed variables, correctly and incorrectly named
     */
    val mixedVars by lazy { correctlyNamedVars + incorrectlyNamedVars }
}
