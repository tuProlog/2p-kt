package it.unibo.tuprolog.libraries.stdlib.rule

import it.unibo.tuprolog.rule.RuleWrapper
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.theory.ClauseDatabase

object SpecificRules {
    val wrappers: Sequence<RuleWrapper<ExecutionContextImpl>> = sequenceOf(
        Catch,
        Call,
        Comma
    )

    val clauseDb: ClauseDatabase = ClauseDatabase.of(
        wrappers.map { it.wrappedImplementation }
    )
}