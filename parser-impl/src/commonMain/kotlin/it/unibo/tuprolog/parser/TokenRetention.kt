package it.unibo.tuprolog.parser

enum class TokenRetention {
    /** Retain every token produced by the source. */
    KEEP_ALL,

    /** Permit a parse session to release tokens after returning a stable snapshot. */
    RELEASE_COMMITTED,
}
