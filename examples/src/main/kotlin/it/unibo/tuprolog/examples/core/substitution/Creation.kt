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
        val substitution = Substitution.of(
            varOf("X") to atomOf("abraham"),
            varOf("Y") to atomOf("isaac")
        )

        println(substitution)
    }
}
