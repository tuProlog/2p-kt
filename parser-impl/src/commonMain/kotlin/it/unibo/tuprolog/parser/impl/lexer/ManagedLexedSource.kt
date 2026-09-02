package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.MaterializedLexedSource

internal interface ManagedLexedSource : LexedSource {
    fun snapshot(
        firstTokenId: Int,
        endExclusiveTokenId: Int,
    ): MaterializedLexedSource

    fun releaseBefore(tokenId: Int)
}
