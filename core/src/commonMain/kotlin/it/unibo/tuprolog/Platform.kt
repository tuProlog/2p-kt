package it.unibo.tuprolog

/** The Kotlin Multiplatform target a 2P-Kt program is currently running on; see [Info.PLATFORM]. */
enum class Platform(
    /** Whether this platform runs on a JVM (including Android). */
    val isJava: Boolean,
    /** Whether this platform runs as JavaScript (Node or a browser). */
    val isJavaScript: Boolean,
) {
    JVM(true, false),
    ANDROID(true, false),
    NODE(false, true),
    BROWSER(false, true),
}
