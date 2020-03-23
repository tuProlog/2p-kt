package it.unibo.tuprolog.solve.library.stdlib.rule

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

    val clauseDb: ClauseDatabase = ClauseDatabase.of(
        wrappers.map { it.wrappedImplementation }
    )
}