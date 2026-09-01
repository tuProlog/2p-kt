package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.toOperatorTable
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.buildParserFor

internal class ClausesParserImpl(
    override val defaultOperatorSet: OperatorSet = OperatorSet.DEFAULT,
    private val lexerOptions: LexerOptions = LexerOptions(),
    private val parserOptions: ParserOptions = ParserOptions(),
) : ClausesParser {
    override fun parseClausesLazily(
        input: String,
        operators: OperatorSet,
    ): Sequence<Clause> =
        buildParserFor(
            input = input,
            lexerOptions = lexerOptions,
            parserOptions = parserOptions,
        ) { parser, lexedSource ->
            val session = parser.openSession(lexedSource, operators.toOperatorTable())
            parseClausesLazily(session)
        }
}
