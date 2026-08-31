package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.sources.MaterializedLexedSource
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.sources.TokenStore
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind

internal class MaterializedLexedSourceImpl(
    records: List<TokenRecord>,
    sourceId: String?,
) : MaterializedLexedSource {
    init {
        require(records.isNotEmpty()) { "A materialized token source cannot be empty" }
        records.zipWithNext().forEach { (first, second) ->
            require(second.token.id == first.token.id + 1) { "Materialized token IDs must be contiguous" }
        }
    }

    private val materializedTokens: List<Token> = records.map(TokenRecord::token)
    private val bySignificantIndex: Map<Int, Token> =
        records.mapNotNull { record -> record.significantIndex?.let { it to record.token } }.toMap()
    private val eofToken: Token? = materializedTokens.firstOrNull { it.kind == TokenKind.END_OF_INPUT }
    private val eofSignificantIndex: Int? =
        records.firstOrNull { it.token.kind == TokenKind.END_OF_INPUT }?.significantIndex
    private val materializedSignificantTokens: List<Token> =
        records
            .asSequence()
            .filter { it.significantIndex != null }
            .map(TokenRecord::token)
            .toList()

    override val source: SourceText =
        SourceText(
            records.joinToString(separator = "") { it.rawText },
            sourceId,
            records
                .first()
                .token.span.start,
        )

    override val tokens: TokenStore = MaterializedTokenStore()

    override val firstSignificantTokenIndex: Int =
        records.firstNotNullOfOrNull(TokenRecord::significantIndex) ?: 0

    override fun significantToken(index: Int): Token =
        bySignificantIndex[index]
            ?: eofToken?.takeIf { index >= eofSignificantIndex!! }
            ?: throw IndexOutOfBoundsException("No significant token with index $index")

    override fun textOf(token: Token): String = source.text(token.span)

    override fun significantTokens(): List<Token> = materializedSignificantTokens

    override fun token(id: Int): Token {
        val index = id - materializedTokens.first().id
        if (index !in materializedTokens.indices) {
            throw IndexOutOfBoundsException("No retained token with ID $id")
        }
        return materializedTokens[index]
    }

    private inner class MaterializedTokenStore : TokenStore {
        override val firstTokenId: Int = materializedTokens.first().id
        override val lastTokenId: Int = materializedTokens.last().id
        override val retainedCount: Int = materializedTokens.size

        override fun get(tokenId: Int): Token = token(tokenId)

        override fun iterator(): Iterator<Token> = materializedTokens.iterator()
    }
}
