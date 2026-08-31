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

interface PrologParser {
    fun parseTerm(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<TermNode>

    fun parseExpression(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<ExpressionNode>

    fun parseClause(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<ClauseNode>

    /** Parses the complete input with a fixed operator table. */
    fun parseTheory(
        input: LexedSource,
        operators: OperatorTable = OperatorTables.empty(),
    ): SyntaxTree<TheoryNode>

    /** Opens a clause-by-clause session whose operator table may change between calls. */
    fun openSession(
        input: LexedSource,
        initialOperators: OperatorTable = OperatorTables.empty(),
    ): PrologParseSession

    /** Opens a clause-by-clause session over asynchronously supplied text chunks. */
    fun openSession(
        input: SuspendingTextChunkSource,
        sourceId: String? = null,
        initialOperators: OperatorTable = OperatorTables.empty(),
        maximumRetainedTokens: Int? = null,
    ): SuspendingPrologParseSession

    companion object {
        fun default(options: ParserOptions = ParserOptions()): PrologParser = PrattPrologParser(options)
    }
}
