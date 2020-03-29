package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.jvm.JvmStatic

interface ClausesParser {
    val defaultOperatorSet: OperatorSet

    fun parseClauseDatabase(input: String, operators: OperatorSet): ClauseDatabase =
        ClauseDatabase.of(parseClausesLazily(input, operators))

    fun parseClauseDatabase(input: String): ClauseDatabase =
        parseClauseDatabase(input, defaultOperatorSet)

    fun parseClausesLazily(input: String, operators: OperatorSet): Sequence<Clause>

    fun parseClausesLazily(input: String): Sequence<Clause> =
        parseClausesLazily(input, defaultOperatorSet)

    fun parseClauses(input: String, operators: OperatorSet): List<Clause> =
        parseClausesLazily(input, operators).toList()

    fun parseClauses(input: String): List<Clause> =
        parseClauses(input, defaultOperatorSet)

    companion object {
        @JvmStatic
        val withNoOperator = withOperators()

        @JvmStatic
        val withStandardOperators = withOperators(OperatorSet.STANDARD)

        @JvmStatic
        val withDefaultOperators = withOperators(OperatorSet.DEFAULT)

        @JvmStatic
        fun withOperators(operators: OperatorSet) = clausesParserWithOperators(operators)

        @JvmStatic
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }
}