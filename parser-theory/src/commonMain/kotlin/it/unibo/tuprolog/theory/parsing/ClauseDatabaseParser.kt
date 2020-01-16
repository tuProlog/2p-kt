package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase

interface ClauseDatabaseParser {
    val defaultOperatorSet: OperatorSet

    fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): ClauseDatabase

    companion object {
//        val withNoOperator: ClauseDatabaseParser =
//            TODO("Use OperatorSet.EMPTY")
//
//        val withStandardOperators: ClauseDatabaseParser =
//            TODO("Use OperatorSet.DEFAULT")
//
//        fun withOperators(operators: OperatorSet): ClauseDatabaseParser =
//            TODO("Use operators")
//
//        fun withOperators(vararg operators: Operator): ClauseDatabaseParser =
//            withOperators(OperatorSet(operators.asSequence()))
    }
}

expect fun ClauseDatabaseParser.Companion.withNoOperator(): ClauseDatabaseParser
expect fun ClauseDatabaseParser.Companion.withStandardOperators() : ClauseDatabaseParser
expect fun ClauseDatabaseParser.Companion.withOperators(operators: OperatorSet): ClauseDatabaseParser
expect fun ClauseDatabaseParser.Companion.withOperators(vararg operators: Operator): ClauseDatabaseParser