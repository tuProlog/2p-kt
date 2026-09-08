package it.unibo.tuprolog.examples.core.substitution

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 * Demonstrates applying a `it.unibo.tuprolog.core.Substitution` to a term via
 * [Substitution.applyTo][it.unibo.tuprolog.core.Substitution.applyTo].
 *
 * A substitution built as `{X -> abraham}` is applied to the term `father(X, isaac)`, replacing
 * every occurrence of the variable `X` with the atom `abraham`. This is the core mechanism used
 * internally by unification and resolution to propagate variable bindings across terms, and is
 * useful to understand in isolation before looking at how `it.unibo.tuprolog.unify.Unificator`
 * and the solvers use it.
 *
 * Running this example prints `father(abraham, isaac)`.
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val term = structOf("father", varOf("X"), atomOf("isaac"))
        val substitution = Substitution.Companion.of(varOf("X") to atomOf("abraham"))

        val result = substitution.applyTo(term)

        println(result)
    }
}
