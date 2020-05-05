package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.theory.ClauseDatabase

object SpecificRules {
    val wrappers = sequenceOf(
        Catch,
        Call,
        Comma,
        Cut,
        NegationAsFailure.Fail,
        NegationAsFailure.Success
    )

    val clauseDb: ClauseDatabase = ClauseDatabase.indexedOf(
        wrappers.map { it.wrappedImplementation }
    )
}