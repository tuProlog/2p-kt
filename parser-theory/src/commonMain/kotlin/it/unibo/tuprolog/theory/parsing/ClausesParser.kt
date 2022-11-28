package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ClausesParser {
    @JsName("defaultOperatorSet")
    val defaultOperatorSet: OperatorSet

    @JsName("parseTheoryWithOperators")
    fun parseTheory(input: String, operators: OperatorSet, unificator: Unificator): Theory =
        Theory.indexedOf(unificator, parseClausesLazily(input, operators))

    @JsName("parseTheoryWithOperatorsAndDefaultUnificator")
    fun parseTheory(input: String, operators: OperatorSet): Theory = parseTheory(input, operators, Unificator.default)

    @JsName("parseTheory")
    fun parseTheory(input: String, unificator: Unificator): Theory =
        parseTheory(input, defaultOperatorSet, unificator)

    @JsName("parseTheoryWithDefaultUnificator")
    fun parseTheory(input: String): Theory = parseTheory(input, Unificator.default)

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
        fun withNoOperator() = withOperators(OperatorSet.EMPTY)

        @JvmStatic
        @JsName("withStandardOperators")
        fun withStandardOperators() = withOperators(OperatorSet.STANDARD)

        @JvmStatic
        @JsName("withDefaultOperators")
        fun withDefaultOperators() = withOperators(OperatorSet.DEFAULT)

        @JvmStatic
        @JsName("withOperatorSet")
        fun withOperators(operators: OperatorSet) = clausesParserWithOperators(operators)

        @JvmStatic
        @JsName("withOperators")
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }
}
