package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet

class TermParserImpl(override val defaultOperatorSet: OperatorSet) : TermParser {

    override fun Term.Companion.parse(input: String, operators: OperatorSet): Term =
        PrologParserFactory.instance.parseExpression(input,operators).accept(PrologExpressionVisitor.instance)
}