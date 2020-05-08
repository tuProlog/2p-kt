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
        val term = structOf("father", varOf("X"), varOf("Y"))

        val sub1 = Substitution.of(varOf("X"), atomOf("abraham"))
        val sub2 = Substitution.of(varOf("Y"), atomOf("isaac"))

        val substitution = sub1 + sub2

        val result = substitution.applyTo(term)
        println(result)
    }
}