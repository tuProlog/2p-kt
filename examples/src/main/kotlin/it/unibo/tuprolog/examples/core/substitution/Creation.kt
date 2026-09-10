package it.unibo.tuprolog.examples.core.substitution

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 * Demonstrates the simplest way of building a `it.unibo.tuprolog.core.Substitution`: via the
 * varargs [Substitution.of][it.unibo.tuprolog.core.Substitution.of] factory, passing a sequence of
 * `Var to Term` pairs.
 *
 * Here `X` is bound to the atom `abraham` and `Y` to the atom `isaac`, producing a substitution
 * with two independent bindings. This is the starting point for the other examples in this
 * package, which show how substitutions can be applied to terms (`Application`), composed
 * (`Composition`), found to be inconsistent (`Contradiction`), or chained (`Chain`).
 *
 * Running this example prints the substitution, e.g. `{X_0=abraham, Y_1=isaac}`.
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val substitution =
            Substitution.of(
                varOf("X") to atomOf("abraham"),
                varOf("Y") to atomOf("isaac"),
            )

        println(substitution)
    }
}
