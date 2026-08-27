package it.unibo.tuprolog.parser.tokens

import it.unibo.tuprolog.parser.sources.SourceSpan

/** A lossless lexical token. Its raw spelling is obtained from its source span. */
data class Token(
    val id: Int,
    val kind: TokenKind,
    val channel: TokenChannel,
    val span: SourceSpan,
    val payload: TokenPayload? = null,
)
