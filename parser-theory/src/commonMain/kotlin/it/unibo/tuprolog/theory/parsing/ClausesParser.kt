package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Parses Prolog source into clauses or a [Theory].
 *
 * This layer converts the concrete syntax produced by `parser-impl` into tuProlog clauses. Each
 * clause gets a fresh variable scope. Recognized `op/3` goals update the session operator table
 * only after their containing clause has been parsed, so they affect subsequent clauses.
 * Low-level syntax failures and invalid operator declarations are exposed as
 * [it.unibo.tuprolog.core.parsing.ParseException], whose `clauseIndex` identifies the zero-based
 * failing clause.
 *
 * ```kotlin
 * val parser = ClausesParser.withDefaultOperators()
 * val theory = parser.parseTheory("parent(alice, bob). ancestor(X, Y) :- parent(X, Y).")
 * ```
 *
 * @property defaultOperatorSet initial operators used by overloads that omit them
 */
interface ClausesParser {
    /** Initial operator set used when no explicit set is supplied. */
    @JsName("defaultOperatorSet")
    val defaultOperatorSet: OperatorSet

    /**
     * Parses [input] eagerly into an indexed theory using [operators] and [unificator].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException if any clause cannot be parsed or an
     * operator declaration is invalid
     */
    @JsName("parseTheoryWithOperators")
    fun parseTheory(
        input: String,
        operators: OperatorSet,
        unificator: Unificator,
    ): Theory = Theory.indexedOf(unificator, parseClausesLazily(input, operators))

    /**
     * Parses [input] using [operators] and the default unificator.
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException if parsing fails
     */
    @JsName("parseTheoryWithOperatorsAndDefaultUnificator")
    fun parseTheory(
        input: String,
        operators: OperatorSet,
    ): Theory = parseTheory(input, operators, Unificator.default)

    /**
     * Parses [input] using [defaultOperatorSet] and [unificator].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException if parsing fails
     */
    @JsName("parseTheory")
    fun parseTheory(
        input: String,
        unificator: Unificator,
    ): Theory = parseTheory(input, defaultOperatorSet, unificator)

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException if [input] cannot be parsed
     */
    @JsName("parseTheoryWithDefaultUnificator")
    fun parseTheory(input: String): Theory = parseTheory(input, Unificator.default)

    /**
     * Returns a single-consumer sequence that parses clauses from [input] using [operators].
     *
     * No clause is parsed until iteration. Operator declarations are applied in iteration order.
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration when a clause fails
     */
    @JsName("parseClausesLazilyWithOperators")
    fun parseClausesLazily(
        input: String,
        operators: OperatorSet,
    ): Sequence<Clause>

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration when a clause fails
     */
    @JsName("parseClausesLazily")
    fun parseClausesLazily(input: String): Sequence<Clause> = parseClausesLazily(input, defaultOperatorSet)

    /**
     * Parses all clauses from [input] eagerly using [operators].
     *
     * @throws it.unibo.tuprolog.core.parsing.ParseException if any clause fails
     */
    @JsName("parseClausesWithOperators")
    fun parseClauses(
        input: String,
        operators: OperatorSet,
    ): List<Clause> = parseClausesLazily(input, operators).toList()

    /**
     * @throws it.unibo.tuprolog.core.parsing.ParseException if any clause in [input] fails
     */
    @JsName("parseClauses")
    fun parseClauses(input: String): List<Clause> = parseClauses(input, defaultOperatorSet)

    companion object {
        /** Creates a parser with no initial operators. */
        @JvmStatic
        @JsName("withNoOperator")
        fun withNoOperator() = withOperators(OperatorSet.EMPTY)

        /** Creates a parser with the standard operator set. */
        @JvmStatic
        @JsName("withStandardOperators")
        fun withStandardOperators() = withOperators(OperatorSet.STANDARD)

        /** Creates a parser with the library's default operator set. */
        @JvmStatic
        @JsName("withDefaultOperators")
        fun withDefaultOperators() = withOperators(OperatorSet.DEFAULT)

        /** Creates a parser using [operators] as its initial operator set. */
        @JvmStatic
        @JsName("withOperatorSet")
        fun withOperators(operators: OperatorSet): ClausesParser = ClausesParserImpl(operators)

        /** Creates a parser from individual initial [operators]. */
        @JvmStatic
        @JsName("withOperators")
        fun withOperators(vararg operators: Operator) = withOperators(OperatorSet(*operators))
    }
}
