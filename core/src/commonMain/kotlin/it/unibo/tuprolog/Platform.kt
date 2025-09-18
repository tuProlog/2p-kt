package it.unibo.tuprolog

enum class Platform(
    val isJava: Boolean,
    val isJavaScript: Boolean,
) {
    JVM(true, false),
    ANDROID(true, false),
    NODE(false, true),
    BROWSER(false, true),
}
