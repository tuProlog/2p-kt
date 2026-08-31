package it.unibo.tuprolog.parser

data class LexerOptions(
    val retention: TokenRetention = TokenRetention.KEEP_ALL,
    /** Optional safety limit for tokens that cannot yet be released. */
    val maximumRetainedTokens: Int? = null,
) {
    init {
        require(maximumRetainedTokens == null || maximumRetainedTokens > 0) {
            "maximumRetainedTokens must be positive when specified"
        }
    }
}
