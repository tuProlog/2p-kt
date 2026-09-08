package it.unibo.tuprolog.examples.core.substitution

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 * Demonstrates walking a chain of variable-to-variable bindings backwards with
 * [Substitution.getOriginal][it.unibo.tuprolog.core.Substitution.getOriginal].
 *
 * The substitution `{X -> Y, Y -> Z}` binds `X` to `Y` and `Y` to `Z`, forming a chain
 * `X -> Y -> Z`. Calling `getOriginal(Z)` follows the chain backwards and retrieves `X`, the
 * variable that was originally substituted to eventually reach `Z`. This is useful when a solver
 * needs to recover the user-facing variable a value should be reported against, after internal
 * renaming has introduced intermediate variables.
 *
 * Running this example prints the substitution itself, followed by the variable `X`.
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val substitution =
            Substitution.of(
                varOf("X") to varOf("Y"),
                varOf("Y") to varOf("Z"),
            )

        val originalZ = substitution.getOriginal(varOf("Z"))

        println(substitution)
        println(originalZ)
    }
}
