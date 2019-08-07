package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ScopeUtils.nonEmptyScopes
import kotlin.test.assertEquals

/**
 * Utils singleton for testing [Scope]
 *
 * @author Enrico
 */
internal object ScopeUtils {

    /** Returns an empty Scope */
    internal val emptyScope
        get() = mutableMapOf<String, Var>()

    /** Contains some non empty variable Scopes */
    internal val nonEmptyScopes
        get() = listOf(
                mutableMapOf("X" to Var.of("X")),
                mutableMapOf("H" to Var.of("H"), "T" to Var.of("T")),
                mutableMapOf("A" to Var.of("A"), "B" to Var.of("B"), "C" to Var.of("C")),
                mutableMapOf("_" to Var.anonymous())
        )


    /** Contains the variables inside [nonEmptyScopes] */
    internal val nonEmptyScopeVars
        get() = nonEmptyScopes.map { it.values.toList() }

    /** Contains the variable names inside [nonEmptyScopes] */
    internal val nonEmptyScopeVarNames
        get() = nonEmptyScopeVars.map { vars -> vars.map { it.name } }

    /** Contains mixed [nonEmptyScopes] and the [emptyScope] */
    internal val mixedScopes
        get() = nonEmptyScopes + emptyScope

    /** Asserts that two Scope Maps have same keys (Var names) and, on the value side, only Var name in common */
    internal fun assertScopeCorrectContents(expected: Map<String, Var>, actual: Map<String, Var>) {
        assertEquals(expected.keys, actual.keys)
        onCorrespondingItems(expected.values, actual.values, VarUtils::assertDifferentVariableExceptForName)
    }
}
