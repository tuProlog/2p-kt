package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet

actual fun termParserWithOperators(operators: OperatorSet): TermParser =
    TermParserImpl(operators)
