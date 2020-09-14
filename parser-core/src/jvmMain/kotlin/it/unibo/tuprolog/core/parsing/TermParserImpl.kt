package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet

class TermParserImpl(override val defaultOperatorSet: OperatorSet) : TermParser {

    override fun parseTerm(input: String, operators: OperatorSet): Term {
        return PrologParserFactory.parseExpression(input, operators).accept(PrologExpressionVisitor())
    }
}
