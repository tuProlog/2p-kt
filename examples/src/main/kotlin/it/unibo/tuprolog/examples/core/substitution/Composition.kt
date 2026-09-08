package it.unibo.tuprolog.examples.core.substitution

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 * Demonstrates combining two independent substitutions with the `+` operator
 * (`it.unibo.tuprolog.core.Substitution.plus`).
 *
 * Two single-variable substitutions, `{X -> abraham}` and `{Y -> isaac}`, are merged into one
 * substitution binding both `X` and `Y`. Because the two substitutions bind disjoint variables,
 * the composition simply unions their bindings (contrast this with the `Contradiction` example in
 * this same package, where composing substitutions that bind the same variable to different
 * values yields a failed substitution). Applying the combined substitution to `father(X, Y)`
 * replaces both variables at once.
 *
 * Running this example prints `father(abraham, isaac)`.
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val term = structOf("father", varOf("X"), varOf("Y"))

        val sub1 = Substitution.of(varOf("X"), atomOf("abraham"))
        val sub2 = Substitution.of(varOf("Y"), atomOf("isaac"))

        val substitution = sub1 + sub2

        val result = substitution.applyTo(term)
        println(result)
    }
}
