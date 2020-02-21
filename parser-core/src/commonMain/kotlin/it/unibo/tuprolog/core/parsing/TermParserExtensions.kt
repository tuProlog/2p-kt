@file:JvmName("TermParserExtensions")

package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.jvm.JvmName

fun termParserWithNoOperator(): TermParser =
    termParserWithOperators(OperatorSet.EMPTY)

fun termParserWithStandardOperators(): TermParser =
    termParserWithOperators(OperatorSet.DEFAULT)

expect fun termParserWithOperators(operators: OperatorSet): TermParser

fun termParserWithOperators(vararg operators: Operator): TermParser =
    termParserWithOperators(OperatorSet(operators.asSequence()))