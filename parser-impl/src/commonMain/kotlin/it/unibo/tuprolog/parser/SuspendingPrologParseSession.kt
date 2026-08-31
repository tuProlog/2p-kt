package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.SyntaxTree

/** Clause-by-clause parsing for sources whose next chunk is obtained asynchronously. */
interface SuspendingPrologParseSession {
    val operators: MutableOperatorTable

    val currentPosition: SourcePosition

    /** True only after EOF has been observed and no candidate clause remains. */
    val isAtEnd: Boolean

    suspend fun parseNextClause(): SyntaxTree<ClauseNode>?

    suspend fun close()
}
