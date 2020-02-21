package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.jvm.JvmStatic

interface ClauseDatabaseParser {
    val defaultOperatorSet: OperatorSet

    fun parseClauseDatabase(input: String, operators: OperatorSet): ClauseDatabase

    fun parseClauseDatabase(input: String): ClauseDatabase =
        parseClauseDatabase(input, defaultOperatorSet)

    fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet): ClauseDatabase =
        parseClauseDatabase(input, operators)

    fun ClauseDatabase.Companion.parse(input: String): ClauseDatabase =
        parseClauseDatabase(input)

    companion object {
        @JvmStatic
        val withNoOperator = clauseDbParserWithNoOperator()

        @JvmStatic
        val withStandardOperators = clauseDbParserWithStandardOperators()

        @JvmStatic
        fun withOperators(operators: OperatorSet) = clauseDbParserWithOperators(operators)

        @JvmStatic
        fun withOperators(vararg operators: Operator) = clauseDbParserWithOperators(*operators)
    }
}