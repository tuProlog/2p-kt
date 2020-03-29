@file:JvmName("ClausesParserExtensions")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.jvm.JvmName

internal expect fun clausesParserWithOperators(operators: OperatorSet): ClausesParser

private val defaultParser = ClausesParser.withDefaultOperators

fun ClauseDatabase.Companion.parse(input: String, operators: OperatorSet): ClauseDatabase =
    defaultParser.parseClauseDatabase(input, operators)

fun ClauseDatabase.Companion.parse(input: String): ClauseDatabase =
    defaultParser.parseClauseDatabase(input)
