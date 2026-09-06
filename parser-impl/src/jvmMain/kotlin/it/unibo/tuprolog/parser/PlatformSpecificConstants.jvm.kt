package it.unibo.tuprolog.parser

internal actual object PlatformSpecificConstants {
    @Suppress("MagicNumber")
    actual val maximumNestingDepth: Int
        // 1024 lets real recursion get close enough to a 1MB thread stack (the JVM's default
        // stack size, and what e.g. Windows and Gradle test workers commonly use) that it
        // sometimes overflows for real before this counter-based guard trips - flaky and
        // JVM/JIT-dependent. 512 leaves enough headroom to always fail gracefully instead.
        get() = 512
}

internal actual inline fun <T> runCatchingStackOverflow(
    onOverflow: () -> T,
    body: () -> T,
): T =
    try {
        body()
    } catch (_: StackOverflowError) {
        onOverflow()
    }
