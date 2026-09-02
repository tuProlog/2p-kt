package it.unibo.tuprolog.parser

internal actual object PlatformSpecificConstants {
    // TODO: find limit value for JS empirically
    @Suppress("MagicNumber")
    actual val maximumNestingDepth: Int
        // 256*3 fails with RangeError on JS (stack overflow), 256*2 works fine, so we use it as a safe value
        get() = 256 * 2
}
