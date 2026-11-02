package it.unibo.tuprolog.core.testutils

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Scope]
 *
 * @author Enrico
 */
internal object ScopeUtils {
    /** Returns an empty Scope */
    internal val emptyScope
        get() = mutableMapOf<String, Var>()

    /** A named variable Scope, for parameterized tests */
    internal data class ScopeCase(
        val id: String,
        val varNames: List<String>,
    ) {
        /** A fresh variable Scope, containing fresh variables named after [varNames] */
        fun scope(): MutableMap<String, Var> = varNames.associateWithTo(mutableMapOf()) { Var.of(it) }
    }

    /** Contains some non empty variable Scopes */
    internal val nonEmptyScopeCases by lazy {
        listOf(
            ScopeCase("one-variable", listOf("X")),
            ScopeCase("two-variables", listOf("H", "T")),
            ScopeCase("three-variables", listOf("A", "B", "C")),
        )
    }

    /** Contains mixed [nonEmptyScopeCases] and the [emptyScope] */
    internal val mixedScopeCases by lazy { nonEmptyScopeCases + ScopeCase("empty", emptyList()) }

    /** Asserts that two Scope Maps have same keys (Var names) and, on the value side, only Var name in common */
    internal fun assertScopeCorrectContents(
        expected: Map<String, Var>,
        actual: Map<String, Var>,
    ) {
        actual.keys shouldBe expected.keys
        for (name in expected.keys) {
            VarUtils.assertDifferentVariableExceptForName(expected.getValue(name), actual.getValue(name))
        }
    }
}
