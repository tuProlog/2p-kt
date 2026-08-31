package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxTree
import it.unibo.tuprolog.parser.tree.TermNode
import it.unibo.tuprolog.parser.tree.TheoryNode
import java.io.Reader

/**
 * Creates a lazy token source backed by [reader]. The caller retains ownership of the reader.
 */
fun PrologLexer.lex(
    reader: Reader,
    sourceId: String? = null,
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    options: LexerOptions = LexerOptions(),
): LexedSource {
    require(chunkSize > 0) { "chunkSize must be positive" }
    val buffer = CharArray(chunkSize)
    return lex(
        TextChunkSource {
            var count: Int
            do {
                count = reader.read(buffer)
            } while (count == 0)
            if (count < 0) null else buffer.concatToString(0, count)
        },
        sourceId,
        options,
    )
}

fun PrologParser.parseTerm(
    reader: Reader,
    operators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = null,
    lexer: PrologLexer = PrologLexer.default(),
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
): SyntaxTree<TermNode> = parseTerm(lexer.lex(reader, sourceId, chunkSize), operators)

fun PrologParser.parseExpression(
    reader: Reader,
    operators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = null,
    lexer: PrologLexer = PrologLexer.default(),
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
): SyntaxTree<ExpressionNode> = parseExpression(lexer.lex(reader, sourceId, chunkSize), operators)

fun PrologParser.parseClause(
    reader: Reader,
    operators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = null,
    lexer: PrologLexer = PrologLexer.default(),
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
): SyntaxTree<ClauseNode> = parseClause(lexer.lex(reader, sourceId, chunkSize), operators)

fun PrologParser.parseTheory(
    reader: Reader,
    operators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = null,
    lexer: PrologLexer = PrologLexer.default(),
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
): SyntaxTree<TheoryNode> = parseTheory(lexer.lex(reader, sourceId, chunkSize), operators)

/**
 * Opens a bounded clause session. Successfully parsed clauses are detached before their input
 * tokens are released. The caller retains ownership of [reader].
 */
fun PrologParser.openSession(
    reader: Reader,
    initialOperators: OperatorTable = OperatorTables.empty(),
    sourceId: String? = null,
    lexer: PrologLexer = PrologLexer.default(),
    chunkSize: Int = DEFAULT_READER_CHUNK_SIZE,
    maximumRetainedTokens: Int? = null,
): PrologParseSession =
    openSession(
        lexer.lex(
            reader,
            sourceId,
            chunkSize,
            LexerOptions(
                retention = TokenRetention.RELEASE_COMMITTED,
                maximumRetainedTokens = maximumRetainedTokens,
            ),
        ),
        initialOperators,
    )

const val DEFAULT_READER_CHUNK_SIZE: Int = 8 * 1024
