package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.sources.SourcePosition

interface PrologParseSession {
    val operators: MutableOperatorTable
    val currentPosition: SourcePosition
    val isAtEnd: Boolean

    fun parseNextClause(): SyntaxTree<ClauseNode>?
}
