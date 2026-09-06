package it.unibo.tuprolog.parser

internal actual object PlatformSpecificConstants {
    // TODO: find limit value for JS empirically
    @Suppress("MagicNumber")
    actual val maximumNestingDepth: Int
        // 256*3 fails with RangeError on JS (stack overflow), 256*2 works fine, so we use it as a safe value
        get() = 256 * 2
}

internal actual inline fun <T> runCatchingStackOverflow(
    onOverflow: () -> T,
    body: () -> T,
): T =
    try {
        body()
    } catch (e: dynamic) {
        if (js("e instanceof RangeError")) {
            onOverflow()
        } else {
            throw e
        }
    }
