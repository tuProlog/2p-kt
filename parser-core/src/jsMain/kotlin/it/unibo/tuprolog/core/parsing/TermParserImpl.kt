package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet

class TermParserImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet
) : TermParser {
    override fun parseTerm(input: String, operators: OperatorSet): Term =
        PrologParserFactory.parseSingletonExpression(input, operators).accept(PrologVisitor(scope))

    override fun parseTerms(input: String, operators: OperatorSet): Sequence<Term> =
        PrologParserFactory.parseExpressions(input, operators).map { it.accept(PrologVisitor(scope)) }
}
