@file:JvmName("ClausesParserExtensions")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmName

internal expect fun clausesParserWithOperators(operators: OperatorSet): ClausesParser

private val defaultParser = ClausesParser.withDefaultOperators()

@JsName("parseTheoryWithOperators")
fun Theory.Companion.parse(input: String, operators: OperatorSet): Theory =
    defaultParser.parseTheory(input, operators)

@JsName("parseTheory")
fun Theory.Companion.parse(input: String): Theory =
    defaultParser.parseTheory(input)

@JsName("parseAsTheoryWithOperators")
fun String.parseAsTheory(operators: OperatorSet): Theory =
    defaultParser.parseTheory(this, operators)

@JsName("parseAsTheory")
fun String.parseAsTheory(): Theory =
    defaultParser.parseTheory(this)

@JsName("parseAsClausesWithOperators")
fun String.parseAsClauses(operators: OperatorSet): List<Clause> =
    defaultParser.parseClauses(this, operators)

@JsName("parseAsClauses")
fun String.parseAsClauses(): List<Clause> =
    defaultParser.parseClauses(this)

@JsName("parseAsClausesLazilyWithOperators")
fun String.parseAsClausesLazily(operators: OperatorSet): Sequence<Clause> =
    defaultParser.parseClausesLazily(this, operators)

@JsName("parseAsClausesLazily")
fun String.parseAsClausesLazily(): Sequence<Clause> =
    defaultParser.parseClausesLazily(this)
