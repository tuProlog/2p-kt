package it.unibo.tuprolog.parser

internal actual object PlatformSpecificConstants {
    @Suppress("MagicNumber")
    actual val maximumNestingDepth: Int
        get() = 1024
}
