package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.PrologParseSession
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.tree.SyntaxTree
import it.unibo.tuprolog.parser.tree.TermNode
import it.unibo.tuprolog.parser.tree.TheoryNode
import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition

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
        return SyntaxTree(input, root, grammar.semanticTokens)
    }

    override fun parseExpression(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<ExpressionNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseSingletonExpression()
        return SyntaxTree(input, root, grammar.semanticTokens)
    }

    override fun parseClause(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<ClauseNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseCompleteClause()
        return SyntaxTree(input, root, grammar.semanticTokens)
    }

    override fun parseTheory(
        input: LexedSource,
        operators: OperatorTable,
    ): SyntaxTree<TheoryNode> {
        val cursor = TokenCursor(input)
        val grammar = PrologGrammar(input, cursor, operators, options)
        val root = grammar.parseTheory()
        return SyntaxTree(input, root, grammar.semanticTokens)
    }

    override fun openSession(
        input: LexedSource,
        initialOperators: OperatorTable,
    ): PrologParseSession = ParseSessionImpl(input, initialOperators, options)
}

private class ParseSessionImpl(
    private val input: LexedSource,
    initialOperators: OperatorTable,
    private val options: ParserOptions,
) : PrologParseSession {
    private val cursor = TokenCursor(input)

    override val operators: MutableOperatorTable =
        OperatorTables.mutableOf(*initialOperators.allDefinitions().toTypedArray())

    override val currentPosition: SourcePosition
        get() = cursor.currentPosition

    override val isAtEnd: Boolean
        get() = cursor.isAtEnd

    override fun parseNextClause(): SyntaxTree<ClauseNode>? {
        if (cursor.isAtEnd) {
            return null
        }
        val mark = cursor.mark()
        return try {
            val grammar = PrologGrammar(input, cursor, operators, options)
            val root = grammar.parseClauseNode()
            SyntaxTree(input, root, grammar.semanticTokens)
        } catch (error: Throwable) {
            cursor.restore(mark)
            throw error
        }
    }
}
