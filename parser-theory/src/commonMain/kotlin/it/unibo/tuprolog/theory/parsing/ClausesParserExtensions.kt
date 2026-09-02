@file:JvmName("ClausesParserExtensions")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

private val defaultParser = ClausesParser.withDefaultOperators()

/**
 * Parses [input] as a theory using [operators].
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
 */
@JsName("parseTheoryWithOperators")
fun Theory.Companion.parse(
    input: String,
    operators: OperatorSet,
): Theory = defaultParser.parseTheory(input, operators)

/**
 * Parses [input] as a theory.
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
 */
@JsName("parseTheory")
fun Theory.Companion.parse(input: String): Theory = defaultParser.parseTheory(input)

/**
 * Parses this string as a theory using [operators].
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
 */
@JsName("parseAsTheoryWithOperators")
fun String.parseAsTheory(operators: OperatorSet): Theory = defaultParser.parseTheory(this, operators)

/**
 * Parses this string as a theory.
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
 */
@JsName("parseAsTheory")
fun String.parseAsTheory(): Theory = defaultParser.parseTheory(this)

/**
 * Parses this string eagerly into clauses.
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
 */
@JsName("parseAsClausesWithOperators")
fun String.parseAsClauses(operators: OperatorSet): List<Clause> = defaultParser.parseClauses(this, operators)

/**
 * Parses this string eagerly into clauses.
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException on failure
 */
@JsName("parseAsClauses")
fun String.parseAsClauses(): List<Clause> = defaultParser.parseClauses(this)

/**
 * Parses this string lazily using [operators].
 *
 * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration when a clause fails
 */
@JsName("parseAsClausesLazilyWithOperators")
fun String.parseAsClausesLazily(operators: OperatorSet): Sequence<Clause> =
    defaultParser.parseClausesLazily(this, operators)

/**
 * @throws it.unibo.tuprolog.core.parsing.ParseException during iteration when a clause fails
 */
@JsName("parseAsClausesLazily")
fun String.parseAsClausesLazily(): Sequence<Clause> = defaultParser.parseClausesLazily(this)
