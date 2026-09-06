package it.unibo.tuprolog.parser

internal expect object PlatformSpecificConstants {
    val maximumNestingDepth: Int
}

/**
 * Runs [body], falling back to [onOverflow] if the platform reports that the call stack was
 * exhausted. This is a safety net for [PlatformSpecificConstants.maximumNestingDepth]: that limit
 * is only an estimate of how deep recursive-descent parsing can go before overflowing the actual
 * call stack, and the true budget varies across JVMs, JVM options, and threads.
 */
internal expect inline fun <T> runCatchingStackOverflow(
    onOverflow: () -> T,
    body: () -> T,
): T
