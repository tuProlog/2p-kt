package it.unibo.tuprolog.examples.core.substitution

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 * Demonstrates that composing two substitutions that bind the same variable to different,
 * non-unifiable values yields a failed substitution
 * (`it.unibo.tuprolog.core.Substitution.Fail`/[Substitution.isFailed][it.unibo.tuprolog.core.Substitution.isFailed]).
 *
 * `{X -> abraham}` and `{X -> nahor}` both bind `X`, but to different atoms, so `sub1 + sub2` is
 * inconsistent and the `+` operator (`it.unibo.tuprolog.core.Substitution.plus`) produces a
 * failed substitution rather than throwing. A failed substitution acts as an identity when
 * applied to a term via [applyTo][it.unibo.tuprolog.core.Substitution.applyTo]: the term is
 * returned unchanged because the (contradictory) bindings cannot be performed. This mirrors what
 * happens internally when unification of two terms fails.
 *
 * Running this example prints the failed substitution, followed by the original term
 * `father(X, isaac)`, unchanged.
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val term = structOf("father", varOf("X"), atomOf("isaac"))

        val sub1 = Substitution.of(varOf("X"), atomOf("abraham"))
        val sub2 = Substitution.of(varOf("X"), atomOf("nahor"))

        val substitution = sub1 + sub2 // contradiction!

        val result = substitution.applyTo(term) // father(X_0, isaac) (substitution could not be performed)

        println(substitution)
        println(result)
    }
}
