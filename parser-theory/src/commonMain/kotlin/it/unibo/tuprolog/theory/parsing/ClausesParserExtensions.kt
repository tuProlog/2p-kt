@file:JvmName("ClausesParserExtensions")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.js.JsName
import kotlin.jvm.JvmName

internal expect fun clausesParserWithOperators(operators: OperatorSet): ClausesParser

private val defaultParser = ClausesParser.withDefaultOperators

@JsName("parseClauseDatabaseWithOperators")
fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet): ClauseDatabase =
    defaultParser.parseClauseDatabase(input, operators)

@JsName("parseClauseDatabase")
fun ClauseDatabase.Companion.parse(input: String): ClauseDatabase =
    defaultParser.parseClauseDatabase(input)

@JsName("parseAsClauseDatabaseWithOperators")
fun String.parseAsClauseDatabase(operators: OperatorSet): ClauseDatabase =
    defaultParser.parseClauseDatabase(this, operators)

@JsName("parseAsClauseDatabase")
fun String.parseAsClauseDatabase(): ClauseDatabase =
    defaultParser.parseClauseDatabase(this)

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