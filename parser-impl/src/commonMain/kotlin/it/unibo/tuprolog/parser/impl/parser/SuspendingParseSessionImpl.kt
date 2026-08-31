package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.SuspendingPrologParseSession
import it.unibo.tuprolog.parser.SuspendingTextChunkSource
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.impl.lexer.IncrementalTokenScanner
import it.unibo.tuprolog.parser.impl.lexer.MaterializedLexedSourceImpl
import it.unibo.tuprolog.parser.impl.lexer.TokenRecord
import it.unibo.tuprolog.parser.operators.MutableOperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTable
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenChannel
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.SyntaxTree

internal class SuspendingParseSessionImpl(
    private val parser: PrologParser,
    input: SuspendingTextChunkSource,
    sourceId: String?,
    initialOperators: OperatorTable,
    maximumRetainedTokens: Int?,
) : SuspendingPrologParseSession {
    private val clauses = SuspendingClauseSource(input, sourceId, maximumRetainedTokens)

    override val operators: MutableOperatorTable =
        OperatorTables.mutableOf(*initialOperators.allDefinitions().toTypedArray())

    override val currentPosition: SourcePosition
        get() = clauses.currentPosition

    override val isAtEnd: Boolean
        get() = clauses.isAtEnd

    override suspend fun parseNextClause(): SyntaxTree<ClauseNode>? {
        val candidate = clauses.nextClause() ?: return null
        return try {
            val x = parser.parseClause(candidate, operators).also { clauses.commit() }
            x
        } catch (error: Throwable) {
            clauses.rollback()
            error.printStackTrace()
            throw error
        }
    }

    override suspend fun close() {
        clauses.close()
    }
}

private class SuspendingClauseSource(
    private val input: SuspendingTextChunkSource,
    sourceId: String?,
    private val maximumRetainedTokens: Int?,
) {
    private val scanner = IncrementalTokenScanner(sourceId)
    private val pending: MutableList<TokenRecord> = mutableListOf()

    private var nextSignificantIndex: Int = 0
    private var inputFinished: Boolean = false
    private var candidate: LexedSource? = null
    private var eofObserved: Boolean = false
    private var terminalFailure: Throwable? = null

    init {
        require(maximumRetainedTokens == null || maximumRetainedTokens > 0) {
            "maximumRetainedTokens must be positive when specified"
        }
    }

    val currentPosition: SourcePosition
        get() =
            pending
                .firstOrNull()
                ?.token
                ?.span
                ?.start ?: scanner.currentPosition

    val isAtEnd: Boolean
        get() = eofObserved && pending.isEmpty() && candidate == null

    suspend fun nextClause(): LexedSource? {
        terminalFailure?.let { throw it }
        candidate?.let { return it }

        while (true) {
            val scanned = scanner.pollToken()
            if (scanned == null) {
                if (inputFinished) {
                    error("Incremental scanner requested input after EOF")
                }
                val chunk =
                    try {
                        input.readChunk()
                    } catch (error: Throwable) {
                        val position = scanner.currentPosition
                        throw SourceReadException(scanner.source, SourceSpan(position, position), error)
                    }
                if (chunk == null) {
                    inputFinished = true
                    scanner.finish()
                } else if (chunk.isNotEmpty()) {
                    scanner.append(chunk)
                }
                continue
            }

            val significantIndex =
                if (scanned.token.channel == TokenChannel.SIGNIFICANT) nextSignificantIndex++ else null
            val record = TokenRecord(scanned.token, scanned.rawText, significantIndex)
            pending += record
            if (maximumRetainedTokens != null && pending.size > maximumRetainedTokens) {
                throw TokenBufferLimitExceededException(
                    scanner.source,
                    record.token.span,
                    maximumRetainedTokens,
                ).also { terminalFailure = it }
            }

            when (scanned.token.kind) {
                TokenKind.FULL_STOP -> {
                    val eofPosition = scanned.token.span.endExclusive
                    val syntheticEof =
                        TokenRecord(
                            Token(
                                id = scanned.token.id + 1,
                                kind = TokenKind.END_OF_INPUT,
                                channel = TokenChannel.SIGNIFICANT,
                                span = SourceSpan(eofPosition, eofPosition),
                            ),
                            "",
                            nextSignificantIndex,
                        )
                    return MaterializedLexedSourceImpl(pending + syntheticEof, scanner.source.id)
                        .also { candidate = it }
                }
                TokenKind.END_OF_INPUT -> {
                    eofObserved = true
                    val hasClauseContent =
                        pending.any {
                            it.token.channel == TokenChannel.SIGNIFICANT &&
                                it.token.kind != TokenKind.END_OF_INPUT
                        }
                    if (!hasClauseContent) {
                        pending.clear()
                        scanner.source.discardBefore(scanner.currentPosition.offset)
                        return null
                    }
                    return MaterializedLexedSourceImpl(pending.toList(), scanner.source.id)
                        .also { candidate = it }
                }
                else -> Unit
            }
        }
    }

    fun commit() {
        check(candidate != null)
        pending.clear()
        candidate = null
        scanner.source.discardBefore(scanner.currentPosition.offset)
    }

    fun rollback() {
        check(candidate != null)
    }

    suspend fun close() {
        input.close()
    }
}
