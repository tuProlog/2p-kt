package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor

class TermParserImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
    private val options: ParserOptions = ParserOptions(),
) : TermParser {
    override fun parseTerm(
        input: String,
        operators: OperatorSet,
    ): Term =
        buildParserFor(input, options = options) { parser, lexedSource ->
            val syntaxTree = parser.parseTerm(lexedSource, operators.toOperatorTable())
            val visitor = PrologTermParserVisitor(scope)
            syntaxTree.root.accept(visitor)
        }
}
