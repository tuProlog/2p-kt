package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.PrologParseSession
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.SuspendingPrologParseSession
import it.unibo.tuprolog.parser.SuspendingTextChunkSource
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxTree
import it.unibo.tuprolog.parser.tree.TermNode
import it.unibo.tuprolog.parser.tree.TheoryNode

internal class PrattPrologParser(
    private val options: ParserOptions,
) : PrologParser {
    override fun parseTerm(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<TermNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseSingletonTerm()
        return SyntaxTree(input.materialize(), root, grammar.semanticTokens)
    }

    override fun parseExpression(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<ExpressionNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseSingletonExpression()
        return SyntaxTree(input.materialize(), root, grammar.semanticTokens)
    }

    override fun parseClause(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<ClauseNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseCompleteClause()
        return SyntaxTree(input.materialize(), root, grammar.semanticTokens)
    }

    override fun parseTheory(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<TheoryNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseTheory()
        return SyntaxTree(input.materialize(), root, grammar.semanticTokens)
    }

    override fun openSession(
        input: LexedSource,
        initialOperators: OperatorTable,
    ): PrologParseSession = ParseSessionImpl(input, initialOperators, options)

    override fun openSession(
        input: SuspendingTextChunkSource,
        sourceId: String?,
        initialOperators: OperatorTable,
        maximumRetainedTokens: Int?,
    ): SuspendingPrologParseSession =
        SuspendingParseSessionImpl(this, input, sourceId, initialOperators, maximumRetainedTokens)
}
