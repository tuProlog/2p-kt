package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase

interface ClauseDatabaseParser {
    val defaultOperatorSet: OperatorSet

    fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): ClauseDatabase {
        TODO("this is platform-specific")
    }

    companion object {
        val withNoOperator: ClauseDatabaseParser =
            TODO("Use OperatorSet.EMPTY")

        val withStandardOperators: ClauseDatabaseParser =
            TODO("Use OperatorSet.DEFAULT")

        fun withOperators(operators: OperatorSet): ClauseDatabaseParser =
            TODO("Use operators")

        fun withOperators(vararg operators: Operator): ClauseDatabaseParser =
            withOperators(OperatorSet(operators.asSequence()))
    }
}