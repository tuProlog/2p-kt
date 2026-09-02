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

    private val materializedTokens: List<Token>
    private val materializedSignificantTokens: List<Token>
    private val firstSignificantIndex: Int
    private val eofToken: Token?
    private val eofSignificantIndex: Int?

    init {
        val tokens = ArrayList<Token>(records.size)
        val significant = ArrayList<Token>(records.size)
        var firstSignificant: Int? = null
        var eof: Token? = null
        var eofSignificant: Int? = null
        for (record in records) {
            val token = record.token
            tokens += token
            val significantIndex = record.significantIndex
            if (significantIndex != null) {
                if (firstSignificant == null) {
                    firstSignificant = significantIndex
                }
                significant += token
            }
            if (token.kind == TokenKind.END_OF_INPUT) {
                eof = token
                eofSignificant = significantIndex
            }
        }
        materializedTokens = tokens
        materializedSignificantTokens = significant
        firstSignificantIndex = firstSignificant ?: 0
        eofToken = eof
        eofSignificantIndex = eofSignificant
    }

    override val source: SourceText =
        SourceText(
            buildString(records.sumOf { it.rawText.length }) {
                records.forEach { append(it.rawText) }
            },
            sourceId,
            records
                .first()
                .token.span.start,
        )

    override val tokens: TokenStore = MaterializedTokenStore()

    override val firstSignificantTokenIndex: Int = firstSignificantIndex

    override fun significantToken(index: Int): Token =
        materializedSignificantTokens
            .getOrNull(index - firstSignificantTokenIndex)
            ?: eofToken?.takeIf { eofSignificantIndex != null && index >= eofSignificantIndex }
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
