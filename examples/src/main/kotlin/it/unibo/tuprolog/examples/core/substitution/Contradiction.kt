package it.unibo.tuprolog.examples.core.substitution

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution

/**
 *
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