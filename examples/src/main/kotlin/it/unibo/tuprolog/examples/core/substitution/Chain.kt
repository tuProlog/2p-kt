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
