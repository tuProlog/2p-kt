package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.toOperatorTable
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

internal class ClausesReaderImpl(
    override val defaultOperatorSet: OperatorSet,
    private val lexerOptions: LexerOptions = LexerOptions(),
    private val parserOptions: ParserOptions = ParserOptions(),
) : ClausesReader {
    override fun readClausesLazily(
        inputStream: InputStream,
        operators: OperatorSet,
    ): Sequence<Clause> = readClausesLazily(InputStreamReader(inputStream), operators)

    override fun readClausesLazily(
        reader: Reader,
        operators: OperatorSet,
    ): Sequence<Clause> =
        buildParserFor(
            input = reader,
            lexerOptions = lexerOptions,
            parserOptions = parserOptions,
        ) { parser, lexedSource ->
            val session = parser.openSession(lexedSource, operators.toOperatorTable())
            parseClausesLazily(session)
        }
}
