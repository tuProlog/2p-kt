package it.unibo.tuprolog.examples.core

import it.unibo.tuprolog.core.*

/**
 *
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