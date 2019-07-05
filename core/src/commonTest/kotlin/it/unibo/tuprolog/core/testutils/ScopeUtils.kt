package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.ScopeUtils.nonEmptyScopes

/**
 * Utils singleton for testing [Scope]
 *
 * @author Enrico
 */
internal object ScopeUtils {

    /**
     * Returns an empty Scope
     */
    internal val emptyScope
        get() = mutableMapOf<String, Var>()

    /**
     * Contains some non empty variable Scopes
     */
    internal val nonEmptyScopes
        get() = listOf(
                mutableMapOf("X" to Var.of("X")),
                mutableMapOf("H" to Var.of("H"), "T" to Var.of("T")),
                mutableMapOf("A" to Var.of("A"), "B" to Var.of("B"), "C" to Var.of("C"))
        )


    /**
     * Contains the variables inside [nonEmptyScopes]
     */
    internal val nonEmptyScopeVars
        get() = nonEmptyScopes.map { it.values.toList() }

    /**
     * Contains the variable names inside [nonEmptyScopes]
     */
    internal val nonEmptyScopeVarNames
        get() = nonEmptyScopeVars.map { vars -> vars.map { it.name } }

    /**
     * Contains mixed [nonEmptyScopes] and the [emptyScope]
     */
    internal val mixedScopes
        get() = nonEmptyScopes + emptyScope

}
