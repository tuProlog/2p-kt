package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet

actual fun TermParser.Companion.withNoOperator(): TermParser =
    TermParserImpl(OperatorSet.EMPTY)

actual fun TermParser.Companion.withStandardOperators(): TermParser =
    TermParserImpl(OperatorSet.DEFAULT)

actual fun TermParser.Companion.withOperators(operators: OperatorSet): TermParser =
    TermParserImpl(operators)

// this can be implemented in common
//actual fun TermParser.Companion.withOperators(vararg operators: Operator): TermParser =
//    TermParserImpl(OperatorSet(operators.asSequence()))