package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxTree

/**
 * Stateful, incremental parsing over one [input].
 *
 * The mutable [operators] table is consulted afresh for every parsed clause or term, allowing a
 * directive processor to update syntax between calls without re-lexing. A failed parse restores
 * [currentPosition]. Successful results own stable source/token snapshots even if the input uses
 * [TokenRetention.RELEASE_COMMITTED]. Sessions are single-consumer and not thread-safe.
 */
interface PrologParseSession {
    /** Lazy token source consumed by this session. */
    val input: LexedSource

    /** Session-local operator table used by subsequent parsing calls. */
    val operators: MutableOperatorTable

    /** Start position of the next candidate term or clause. */
    val currentPosition: SourcePosition

    /** Whether EOF has been observed after the last successfully parsed item. */
    val isAtEnd: Boolean

    /**
     * Parses and commits the next full-stop-terminated clause, or returns `null` at EOF.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if the next clause fails
     */
    fun parseNextClause(): SyntaxTree<ClauseNode>?

    /**
     * Parses and commits the next term, which must end at EOF or a full-stop separator.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if the next term fails
     */
    fun parseNextTerm(): SyntaxTree<ExpressionNode>?
}
