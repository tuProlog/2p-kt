package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet

actual fun termParserWithNoOperator(): TermParser =
    TermParserImpl(OperatorSet.EMPTY)

actual fun termParserWithStandardOperators(): TermParser =
    TermParserImpl(OperatorSet.DEFAULT)

actual fun termParserWithOperators(operators: OperatorSet): TermParser =
    TermParserImpl(operators)