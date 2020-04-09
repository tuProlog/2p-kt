package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ClausesParser {
    @JsName("defaultOperatorSet")
    val defaultOperatorSet: OperatorSet

    @JsName("parseClauseDatabaseWithOperators")
    fun parseClauseDatabase(input: String, operators: OperatorSet): ClauseDatabase =
        ClauseDatabase.of(parseClausesLazily(input, operators))

    @JsName("parseClauseDatabase")
    fun parseClauseDatabase(input: String): ClauseDatabase =
        parseClauseDatabase(input, defaultOperatorSet)

    @JsName("parseClausesLazilyWithOperators")
    fun parseClausesLazily(input: String, operators: OperatorSet): Sequence<Clause>

    @JsName("parseClausesLazily")
    fun parseClausesLazily(input: String): Sequence<Clause> =
        parseClausesLazily(input, defaultOperatorSet)

    @JsName("parseClausesWithOperators")
    fun parseClauses(input: String, operators: OperatorSet): List<Clause> =
        parseClausesLazily(input, operators).toList()

    @JsName("parseClauses")
    fun parseClauses(input: String): List<Clause> =
        parseClauses(input, defaultOperatorSet)

    companion object {
        @JvmStatic
        @JsName("withNoOperator")
        val withNoOperator = withOperators()

        @JvmStatic
        @JsName("withStandardOperators")
        val withStandardOperators = withOperators(OperatorSet.STANDARD)

        @JvmStatic
        @JsName("withDefaultOperators")
        val withDefaultOperators = withOperators(OperatorSet.DEFAULT)

        @JvmStatic
        @JsName("withOperatorSet")
        fun withOperators(operators: OperatorSet) = clausesParserWithOperators(operators)

        @JvmStatic
        @JsName("withOperators")
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }
}