package it.unibo.tuprolog.solve.problogimpl.stdlib.rule

import it.unibo.tuprolog.theory.Theory

object SpecificRules {
    val wrappers = sequenceOf(
        Catch,
        Call,
        Comma,
        Cut,
        NegationAsFailure.Fail,
        NegationAsFailure.Success,
    )

    val theory: Theory = Theory.indexedOf(
        wrappers.map { it.wrappedImplementation }
    )
}
