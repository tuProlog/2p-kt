package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.theory.Theory

object SpecificRules {
    private val wrappers = sequenceOf(
        Catch,
        Call,
        Comma,
        // Cut, todo uncomment when handled
        NegationAsFailure.Fail,
        NegationAsFailure.Success
    )

    val theory: Theory = Theory.indexedOf(
        wrappers.map { it.implementation }
    )
}
