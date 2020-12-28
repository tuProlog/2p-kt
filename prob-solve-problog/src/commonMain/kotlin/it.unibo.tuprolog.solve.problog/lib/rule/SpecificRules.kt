package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.theory.Theory

object SpecificRules {
    private val wrappers = sequenceOf(
        Prob.And,
        Prob.Or,
        Prob.Arrow,
        Prob.Disjunction,
        Prob.Negation,
        ProbQuery,
        ProbSolveConditional,
        Prob.Prolog,
    )

    val theory: Theory = Theory.indexedOf(
        wrappers.map { it.wrappedImplementation }
    )
}
