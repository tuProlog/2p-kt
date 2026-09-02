package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.impl.parser.PrattPrologParser
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxTree
import it.unibo.tuprolog.parser.tree.TermNode
import it.unibo.tuprolog.parser.tree.TheoryNode

/**
 * Parses a [LexedSource] into an immutable, lossless concrete syntax tree.
 *
 * Structural syntax is handled by predictive recursive descent, while dynamic prefix, infix, and
 * postfix expressions use precedence climbing in the style of Pratt's
 * [top-down operator-precedence parser](https://doi.org/10.1145/512927.512931). Prolog's seven
 * operator specifiers and their `x`/`y` operand constraints follow
 * [ISO/IEC 13211-1](https://www.iso.org/standard/21413.html).
 *
 * Implementations are stateless and reusable. Inputs, operator tables, and sessions may be
 * stateful and are not intended for concurrent access.
 *
 * @see PrologLexer
 * @see PrologParseSession
 */
interface PrologParser {
    /**
     * Parses one non-operator term, optionally followed by a full stop, and requires EOF afterward.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if lexing or parsing fails
     */
    fun parseTerm(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<TermNode>

    /**
     * Parses one expression, optionally followed by a full stop, and requires EOF afterward.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if lexing or parsing fails
     */
    fun parseExpression(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<ExpressionNode>

    /**
     * Parses exactly one clause, including its required terminating full stop.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if lexing or parsing fails
     */
    fun parseClause(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<ClauseNode>

    /**
     * Parses the complete input as zero or more clauses with a fixed operator table.
     *
     * This operation does not execute `op/3` directives.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologSyntaxException if lexing or parsing fails
     */
    fun parseTheory(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<TheoryNode>

    /**
     * Opens a clause-by-clause session whose operator table may change between calls.
     *
     * [initialOperators] is copied, so later changes to it do not affect the session.
     */
    fun openSession(
        input: LexedSource,
        initialOperators: OperatorTable = OperatorTables.empty(),
    ): PrologParseSession

    /**
     * Opens a clause-by-clause session over asynchronously supplied text chunks.
     *
     * ```kotlin
     * val session = parser.openSession(chunks, sourceId = "program.pl", maximumRetainedTokens = 4096)
     * try {
     *     while (!session.isAtEnd) consume(session.parseNextClause() ?: break)
     * } finally {
     *     session.close()
     * }
     * ```
     *
     * @throws IllegalArgumentException if [maximumRetainedTokens] is not positive
     */
    fun openSession(
        input: SuspendingTextChunkSource,
        sourceId: String? = null,
        initialOperators: OperatorTable = OperatorTables.empty(),
        maximumRetainedTokens: Int? = null,
    ): SuspendingPrologParseSession

    companion object {
        /** Returns a stateless parser configured with [options]. */
        fun default(options: ParserOptions = ParserOptions()): PrologParser = PrattPrologParser(options)
    }
}
