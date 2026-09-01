package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException

class TermParserImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
    private val lexerOptions: LexerOptions = LexerOptions(),
    private val parserOptions: ParserOptions = ParserOptions(),
) : TermParser {
    override fun parseTerm(
        input: String,
        operators: OperatorSet,
    ): Term =
        buildParserFor(input, null, lexerOptions, parserOptions) { parser, lexedSource ->
            try {
                val syntaxTree = parser.parseExpression(lexedSource, operators.toOperatorTable())
                val visitor = PrologTermParserVisitor(scope)
                syntaxTree.root.accept(visitor)
            } catch (e: PrologSyntaxException) {
                throw ParseException(
                    input = input,
                    offendingSymbol = e.offendingText,
                    line = e.span.start.line + 1,
                    column = e.span.start.column + 1,
                    message = e.message,
                    throwable = e,
                )
            }
        }
}
