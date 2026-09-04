package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException

/**
 * Configurable default implementation of [TermParser].
 *
 * Most callers should use a [TermParser] factory. Construct this class directly when lexer or
 * parser safety options must be customized.
 *
 * @property scope scope used to create terms and preserve variable identity within one parse
 * @property defaultOperatorSet operators used by overloads that do not receive an explicit set
 * @param lexerOptions low-level lazy-lexer and token-retention configuration
 * @param parserOptions low-level nesting and ambiguity configuration
 */
class TermParserImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
    private val lexerOptions: LexerOptions = LexerOptions(),
    private val parserOptions: ParserOptions = ParserOptions(),
) : TermParser {
    /**
     * Parses [input] as one expression and converts it to a tuProlog term.
     *
     * @throws ParseException if lexing or parsing fails; the typed low-level failure is its cause
     */
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
                throw e.toParseException(input)
            }
        }
}
