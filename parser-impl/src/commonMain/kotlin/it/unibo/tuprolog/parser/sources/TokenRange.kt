package it.unibo.tuprolog.parser.sources

/**
 * End-exclusive range of absolute token identifiers in a [LexedSource].
 *
 * @property startInclusive first token ID in the range
 * @property endExclusive first token ID outside the range
 * @throws IllegalArgumentException if either bound is negative or the end precedes the start
 */
data class TokenRange(
    val startInclusive: Int,
    val endExclusive: Int,
) {
    init {
        require(startInclusive >= 0)
        require(endExclusive >= startInclusive)
    }
}
