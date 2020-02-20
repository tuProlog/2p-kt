package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase

interface ClauseDatabaseParser {
    val defaultOperatorSet: OperatorSet

    fun parseClauseDatabase(input: String, operators: OperatorSet = defaultOperatorSet): ClauseDatabase

    fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet = defaultOperatorSet): ClauseDatabase =
        parseClauseDatabase(input, operators)

    companion object {
        val withNoOperator = clauseDbParserWithNoOperator()

        val withStandardOperators = clauseDbParserWithStandardOperators()

        fun withOperators(operators: OperatorSet) = clauseDbParserWithOperators(operators)

        fun withOperators(vararg operators: Operator) = clauseDbParserWithOperators(*operators)
    }
}

fun clauseDbParserWithNoOperator(): ClauseDatabaseParser =
    clauseDbParserWithOperators(OperatorSet.EMPTY)

fun clauseDbParserWithStandardOperators() : ClauseDatabaseParser =
    clauseDbParserWithOperators(OperatorSet.DEFAULT)

expect fun clauseDbParserWithOperators(operators: OperatorSet): ClauseDatabaseParser

fun clauseDbParserWithOperators(vararg operators: Operator): ClauseDatabaseParser =
    clauseDbParserWithOperators(OperatorSet(*operators))