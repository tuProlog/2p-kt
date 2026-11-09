package it.unibo.tuprolog.solve

/**
 * Members [SolutionStream] has on one platform only: `stream()` on the JVM, a `java.util.stream.Stream` that closes
 * this one when it is closed.
 */
expect interface PlatformSolutionStream

internal expect fun installPlatformHooks(stream: AbstractSolutionStream)
