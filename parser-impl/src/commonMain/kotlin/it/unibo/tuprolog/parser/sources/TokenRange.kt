package it.unibo.tuprolog.parser.sources

/** End-exclusive range of token identifiers in a [LexedSource]. */
data class TokenRange(
    val startInclusive: Int,
    val endExclusive: Int,
) {
    init {
        require(startInclusive >= 0)
        require(endExclusive >= startInclusive)
    }
}
