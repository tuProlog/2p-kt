package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxTree

interface PrologParseSession {
    val input: LexedSource
    val operators: MutableOperatorTable
    val currentPosition: SourcePosition
    val isAtEnd: Boolean

    fun parseNextClause(): SyntaxTree<ClauseNode>?

    fun parseNextTerm(): SyntaxTree<ExpressionNode>?
}
