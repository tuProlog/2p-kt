package it.unibo.tuprolog.parser.tokens

import it.unibo.tuprolog.parser.Representable
import it.unibo.tuprolog.parser.sources.SourceSpan

/**
 * A lossless lexical token.
 *
 * Raw spelling is obtained from [span] through the owning
 * [it.unibo.tuprolog.parser.sources.LexedSource]. [toRepresentation] instead returns normalized
 * content and is not a substitute for source extraction.
 *
 * @property id absolute, monotonically increasing token identifier
 * @property kind operator-independent lexical category
 * @property channel whether this token participates in grammar parsing
 * @property span end-exclusive range in the complete source
 * @property payload decoded or variable token data, when applicable
 */
data class Token(
    val id: Int,
    val kind: TokenKind,
    val channel: TokenChannel,
    val span: SourceSpan,
    val payload: TokenPayload? = null,
) : Representable {
    /** Returns the token's fixed spelling or normalized payload representation. */
    override fun toRepresentation(): String = kind.constant ?: payload?.toRepresentation() ?: "<?>"
}
