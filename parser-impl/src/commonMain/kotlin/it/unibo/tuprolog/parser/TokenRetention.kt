package it.unibo.tuprolog.parser

/** Controls how a lazy [it.unibo.tuprolog.parser.sources.LexedSource] retains consumed input. */
enum class TokenRetention {
    /** Retain every token produced by the source. */
    KEEP_ALL,

    /** Permit a parse session to release tokens after returning a stable snapshot. */
    RELEASE_COMMITTED,
}
