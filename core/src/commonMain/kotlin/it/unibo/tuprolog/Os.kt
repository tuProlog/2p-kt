package it.unibo.tuprolog

enum class Os(
    val isPosix: Boolean,
) {
    WINDOWS(false),
    LINUX(true),
    MAC(true),
    ANDROID(true),
    UNKNOWN_POSIX(true),
    ;

    companion object {
        fun detect(description: String): Os? =
            when {
                description.contains("android", ignoreCase = true) -> ANDROID
                description.contains("dalvik", ignoreCase = true) -> ANDROID
                description.contains("win", ignoreCase = true) -> WINDOWS
                description.contains("mac", ignoreCase = true) -> MAC
                description.contains("mac", ignoreCase = true) -> MAC
                description.contains("linux", ignoreCase = true) -> LINUX
                else -> null
            }
    }
}
