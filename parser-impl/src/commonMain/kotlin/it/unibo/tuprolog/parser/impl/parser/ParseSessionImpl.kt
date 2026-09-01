package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.PrologParseSession
import it.unibo.tuprolog.parser.impl.lexer.ManagedLexedSource
import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxTree

internal class ParseSessionImpl(
    private val input: LexedSource,
    initialOperators: OperatorTable,
    private val options: ParserOptions,
) : PrologParseSession {
    private val cursor = TokenCursor(input)
    private var startTokenId: Int? = null

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
        val firstTokenId = startTokenId ?: input.tokens.firstTokenId
        return try {
            val grammar = PrologGrammar(input, cursor, operators, options)
            val root = grammar.parseClauseNode()
            val endExclusiveTokenId = root.terminatorTokenId + 1
            val stableInput =
                (input as? ManagedLexedSource)?.snapshot(firstTokenId, endExclusiveTokenId)
                    ?: input.materialize()
            startTokenId = endExclusiveTokenId
            (input as? ManagedLexedSource)?.releaseBefore(endExclusiveTokenId)
            SyntaxTree(stableInput, root, grammar.semanticTokens)
        } catch (error: Throwable) {
            cursor.restore(mark)
            throw error
        }
    }

    override fun parseNextTerm(): SyntaxTree<ExpressionNode>? {
        if (cursor.isAtEnd) {
            return null
        }
        val mark = cursor.mark()
        val firstTokenId = startTokenId ?: input.tokens.firstTokenId
        return try {
            val grammar = PrologGrammar(input, cursor, operators, options)
            val (root, endExclusiveTokenId) = grammar.parseSessionExpression()
            val stableInput =
                (input as? ManagedLexedSource)?.snapshot(firstTokenId, endExclusiveTokenId)
                    ?: input.materialize()
            startTokenId = endExclusiveTokenId
            (input as? ManagedLexedSource)?.releaseBefore(endExclusiveTokenId)
            SyntaxTree(stableInput, root, grammar.semanticTokens)
        } catch (error: Throwable) {
            cursor.restore(mark)
            throw error
        }
    }
}
