package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.operators.OperatorSet

actual fun TermParser.Companion.withNoOperator(): TermParser =
    TermParserImpl(OperatorSet.EMPTY)

actual fun TermParser.Companion.withStandardOperators(): TermParser {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}