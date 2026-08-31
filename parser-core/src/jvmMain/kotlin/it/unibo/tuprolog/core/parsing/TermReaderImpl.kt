package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class TermReaderImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
    private val lexerOptions: LexerOptions = LexerOptions(),
    private val parserOptions: ParserOptions = ParserOptions(),
) : TermReader {
    override fun readTerm(
        reader: Reader,
        operators: OperatorSet,
    ): Term? = readTerms(reader, operators).firstOrNull()

    override fun readTerm(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Term? = readTerms(inputStream, operators).firstOrNull()

    override fun readTerms(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Term> =
        buildParserFor(reader, lexerOptions, parserOptions) { parser, lexedSource ->
            val session = parser.openSession(lexedSource)
            val visitor = PrologTermParserVisitor(scope)
            var term = session.parseNextTerm()
            sequence {
                while (term != null) {
                    yield(term!!.root.accept(visitor))
                    term = session.parseNextTerm()
                }
            }
        }

    override fun readTerms(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Term> = readTerms(InputStreamReader(inputStream), operators)
}
