package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.sources.SourceText

class TermParserImpl(
    override val scope: Scope,
    override val defaultOperatorSet: OperatorSet,
    private val options: ParserOptions = ParserOptions()
) : TermParser {
    override fun parseTerm(
        input: String,
        operators: OperatorSet,
    ): Term {
        val source = SourceText(input)
        val lexer = PrologLexer.Companion.default()
        val lexedSource = lexer.lex(source)
        val parser = PrologParser.default(options)
        val syntaxTree = parser.parseTerm(lexedSource, operators.toOperatorTable())
        val visitor = ParserVisitor<Term>(scope)
        return syntaxTree.root.accept(visitor)
    }
}
