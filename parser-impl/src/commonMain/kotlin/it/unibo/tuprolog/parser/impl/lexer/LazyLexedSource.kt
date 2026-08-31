package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.TextChunkSource
import it.unibo.tuprolog.parser.TokenRetention
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.sources.MaterializedLexedSource
import it.unibo.tuprolog.parser.sources.Source
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenStore
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenChannel
import it.unibo.tuprolog.parser.tokens.TokenKind

internal class LazyLexedSource(
    sourceId: String?,
    private val chunks: TextChunkSource,
    private val options: LexerOptions,
) : ManagedLexedSource {
    private val scanner = IncrementalTokenScanner(sourceId)
    private val records: MutableList<TokenRecord> = mutableListOf()
    private val significantTokenIds: MutableList<Int> = mutableListOf()

    private var baseTokenId: Int = 0
    private var baseSignificantIndex: Int = 0
    private var nextSignificantIndex: Int = 0
    private var eofRecord: TokenRecord? = null
    private var sourceFinished: Boolean = false
    private var terminalFailure: Throwable? = null

    override val source: Source
        get() = scanner.source

    override val tokens: TokenStore = LazyTokenStore()

    override val firstSignificantTokenIndex: Int
        get() {
            ensureSignificantToken(baseSignificantIndex)
            return baseSignificantIndex
        }

    override fun significantToken(index: Int): Token {
        require(index >= baseSignificantIndex) {
            "Significant token $index was released; first retained index is $baseSignificantIndex"
        }
        ensureSignificantToken(index)
        val eof = eofRecord
        if (eof != null && index >= eof.significantIndex!!) {
            return eof.token
        }
        return token(significantTokenIds[index - baseSignificantIndex])
    }

    override fun textOf(token: Token): String = record(token.id).rawText

    override fun significantTokens(): List<Token> {
        ensureEndOfInput()
        return significantTokenIds.map(::token)
    }

    override fun materialize(): MaterializedLexedSource {
        ensureEndOfInput()
        return snapshot(baseTokenId, records.last().token.id + 1)
    }

    override fun snapshot(
        firstTokenId: Int,
        endExclusiveTokenId: Int,
    ): MaterializedLexedSource {
        require(firstTokenId >= baseTokenId)
        require(endExclusiveTokenId >= firstTokenId)
        if (endExclusiveTokenId > firstTokenId) {
            ensureToken(endExclusiveTokenId - 1)
        }
        val from = firstTokenId - baseTokenId
        val until = endExclusiveTokenId - baseTokenId
        require(until <= records.size) {
            "Token range $firstTokenId until $endExclusiveTokenId is not available"
        }
        val selected = records.subList(from, until).toList()
        val stableRecords =
            if (selected.last().token.kind == TokenKind.END_OF_INPUT) {
                selected
            } else {
                val eofPosition =
                    selected
                        .last()
                        .token.span.endExclusive
                val eofSignificantIndex =
                    selected.asReversed().firstNotNullOf(TokenRecord::significantIndex) + 1
                selected +
                    TokenRecord(
                        Token(
                            id = endExclusiveTokenId,
                            kind = TokenKind.END_OF_INPUT,
                            channel = TokenChannel.SIGNIFICANT,
                            span = SourceSpan(eofPosition, eofPosition),
                        ),
                        "",
                        eofSignificantIndex,
                    )
            }
        return MaterializedLexedSourceImpl(stableRecords, source.id)
    }

    override fun releaseBefore(tokenId: Int) {
        if (options.retention != TokenRetention.RELEASE_COMMITTED || tokenId <= baseTokenId) {
            return
        }
        val removeCount = (tokenId - baseTokenId).coerceAtMost(records.size)
        if (removeCount == 0) {
            return
        }
        val removed = records.subList(0, removeCount).toList()
        records.subList(0, removeCount).clear()
        baseTokenId += removeCount

        val removedSignificant = removed.count { it.significantIndex != null }
        if (removedSignificant > 0) {
            significantTokenIds.subList(0, removedSignificant).clear()
            baseSignificantIndex += removedSignificant
        }

        val discardOffset =
            records
                .firstOrNull()
                ?.token
                ?.span
                ?.start
                ?.offset ?: scanner.currentPosition.offset
        scanner.source.discardBefore(discardOffset)
    }

    private fun record(tokenId: Int): TokenRecord {
        ensureToken(tokenId)
        require(tokenId >= baseTokenId) {
            "Token $tokenId was released; first retained token is $baseTokenId"
        }
        val index = tokenId - baseTokenId
        if (index !in records.indices) {
            throw IndexOutOfBoundsException("No token with ID $tokenId")
        }
        return records[index]
    }

    private fun ensureToken(tokenId: Int) {
        require(tokenId >= baseTokenId) {
            "Token $tokenId was released; first retained token is $baseTokenId"
        }
        while (tokenId - baseTokenId >= records.size) {
            val eof = eofRecord
            if (eof != null) {
                throw IndexOutOfBoundsException("No token with ID $tokenId; EOF has ID ${eof.token.id}")
            }
            produceToken()
        }
    }

    private fun ensureSignificantToken(index: Int) {
        while (index - baseSignificantIndex >= significantTokenIds.size && eofRecord == null) {
            produceToken()
        }
    }

    private fun ensureEndOfInput() {
        while (eofRecord == null) {
            produceToken()
        }
    }

    private fun produceToken() {
        terminalFailure?.let { throw it }
        while (true) {
            val scanned = scanner.pollToken()
            if (scanned != null) {
                val significantIndex =
                    if (scanned.token.channel == TokenChannel.SIGNIFICANT) nextSignificantIndex++ else null
                val record = TokenRecord(scanned.token, scanned.rawText, significantIndex)
                records += record
                if (significantIndex != null) {
                    significantTokenIds += scanned.token.id
                }
                if (scanned.token.kind == TokenKind.END_OF_INPUT) {
                    eofRecord = record
                }
                enforceLimit(record)
                return
            }

            if (sourceFinished) {
                error("Incremental scanner requested input after the source was finished")
            }
            val chunk =
                try {
                    chunks.readChunk()
                } catch (error: Throwable) {
                    val position = scanner.currentPosition
                    throw SourceReadException(scanner.source, SourceSpan(position, position), error)
                }
            if (chunk == null) {
                sourceFinished = true
                scanner.finish()
            } else if (chunk.isNotEmpty()) {
                scanner.append(chunk)
            }
        }
    }

    private fun enforceLimit(latest: TokenRecord) {
        val maximum = options.maximumRetainedTokens ?: return
        if (records.size > maximum) {
            throw TokenBufferLimitExceededException(scanner.source, latest.token.span, maximum)
                .also { terminalFailure = it }
        }
    }

    private inner class LazyTokenStore : TokenStore {
        override val firstTokenId: Int
            get() {
                if (records.isEmpty()) {
                    produceToken()
                }
                return baseTokenId
            }

        override val lastTokenId: Int
            get() {
                ensureEndOfInput()
                return records.last().token.id
            }

        override val retainedCount: Int
            get() = records.size

        override fun get(tokenId: Int): Token = record(tokenId).token

        override fun iterator(): Iterator<Token> =
            object : Iterator<Token> {
                private var nextId: Int = firstTokenId
                private var finished: Boolean = false

                override fun hasNext(): Boolean = !finished

                override fun next(): Token {
                    if (finished) {
                        throw NoSuchElementException()
                    }
                    val token = get(nextId++)
                    if (token.kind == TokenKind.END_OF_INPUT) {
                        finished = true
                    }
                    return token
                }
            }
    }
}
