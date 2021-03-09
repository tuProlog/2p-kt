package it.unibo.tuprolog.solve.problog.lib.rules

import it.unibo.tuprolog.theory.Theory

internal object SpecificRules {
    private val wrappers = sequenceOf(
        Prob,
    )

    val theory: Theory = Theory.indexedOf(
        wrappers.map { it.wrappedImplementation }
    )
}
