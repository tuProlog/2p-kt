package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

/**
 * Configurable JVM implementation of [TermReader].
 *
 * Prefer a [TermReader] factory unless custom lexer or parser limits are required.
 *
 * @property scope scope used to construct terms
 * @property defaultOperatorSet default operators for overloads that omit them
 * @param lexerOptions lazy-lexer and retained-token configuration
 * @param parserOptions nesting and ambiguity configuration
 */
class TermReaderImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
    private val lexerOptions: LexerOptions = LexerOptions(),
    private val parserOptions: ParserOptions = ParserOptions(),
) : TermReader {
    /**
     * @throws ParseException if the next term cannot be read or parsed
     */
    override fun readTerm(
        reader: Reader,
        operators: OperatorSet,
    ): Term? = readTerms(reader, operators).firstOrNull()

    /**
     * @throws ParseException if the next term cannot be read or parsed
     */
    override fun readTerm(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Term? = readTerms(inputStream, operators).firstOrNull()

    /**
     * @throws ParseException during iteration if a term cannot be read or parsed
     */
    override fun readTerms(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Term> =
        buildParserFor(
            input = reader,
            lexerOptions = lexerOptions,
            parserOptions = parserOptions,
        ) { parser, lexedSource ->
            val session = parser.openSession(lexedSource, operators.toOperatorTable())
            val visitor = PrologTermParserVisitor(scope)
            sequence {
                try {
                    var term = session.parseNextTerm()
                    while (term != null) {
                        yield(term.root.accept(visitor))
                        term = session.parseNextTerm()
                    }
                } catch (e: PrologSyntaxException) {
                    val input = lexedSource.source.let { it.id ?: it.text() }
                    throw e.toParseException(input)
                }
            }
        }

    /**
     * @throws ParseException during iteration if a term cannot be read or parsed
     */
    override fun readTerms(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Term> = readTerms(InputStreamReader(inputStream), operators)
}
