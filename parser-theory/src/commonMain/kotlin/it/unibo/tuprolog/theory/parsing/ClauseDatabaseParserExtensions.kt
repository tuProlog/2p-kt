@file:JvmName("ClauseDatabaseParserExtensions")

package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.jvm.JvmName

fun clauseDbParserWithNoOperator(): ClauseDatabaseParser =
    clauseDbParserWithOperators(OperatorSet.EMPTY)

fun clauseDbParserWithStandardOperators() : ClauseDatabaseParser =
    clauseDbParserWithOperators(OperatorSet.DEFAULT)

expect fun clauseDbParserWithOperators(operators: OperatorSet): ClauseDatabaseParser

fun clauseDbParserWithOperators(vararg operators: Operator): ClauseDatabaseParser =
    clauseDbParserWithOperators(OperatorSet(*operators))