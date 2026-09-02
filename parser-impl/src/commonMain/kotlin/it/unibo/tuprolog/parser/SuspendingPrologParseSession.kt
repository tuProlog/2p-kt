package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.SyntaxTree

/**
 * Stateful clause-by-clause parsing whose input chunks are obtained asynchronously.
 *
 * The session is single-consumer and its operator table may be updated between calls. Always call
 * [close], preferably from `finally`, to release the underlying asynchronous source.
 */
interface SuspendingPrologParseSession {
    /** Session-local operators used for subsequent clauses. */
    val operators: MutableOperatorTable

    /** Start position of the next candidate clause. */
    val currentPosition: SourcePosition

    /** True only after EOF has been observed and no candidate clause remains. */
    val isAtEnd: Boolean

    /**
     * Parses and commits the next clause, or returns `null` at EOF.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if reading, lexing, or
     * parsing the next clause fails
     */
    suspend fun parseNextClause(): SyntaxTree<ClauseNode>?

    /** Closes the underlying chunk source; repeated calls are safe. */
    suspend fun close()
}
