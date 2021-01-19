package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.theory.Theory

internal object SpecificRules {
    private val wrappers = sequenceOf(
        Prob.Negation.Not,
        Prob.Negation.NegationAsFailure,
        Prob.Prolog,
    )

    val theory: Theory = Theory.indexedOf(
        wrappers.map { it.wrappedImplementation }
    )
}
