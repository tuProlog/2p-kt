package it.unibo.tuprolog.parser

/**
 * Configures lazy token production and retention.
 *
 * @property retention policy controlling whether committed stream prefixes may be discarded
 * @property maximumRetainedTokens optional positive limit for tokens that cannot yet be released;
 * exceeding it produces a
 * [it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException]
 * @throws IllegalArgumentException if [maximumRetainedTokens] is not positive
 */
data class LexerOptions(
    val retention: TokenRetention = TokenRetention.KEEP_ALL,
    val maximumRetainedTokens: Int? = null,
) {
    init {
        require(maximumRetainedTokens == null || maximumRetainedTokens > 0) {
            "maximumRetainedTokens must be positive when specified"
        }
    }
}
