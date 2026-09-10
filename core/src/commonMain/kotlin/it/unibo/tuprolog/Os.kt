package it.unibo.tuprolog

/** The operating system a 2P-Kt program is currently running on; see [Info.OS]. */
enum class Os(
    /** Whether this OS exposes a POSIX-like environment. */
    val isPosix: Boolean,
) {
    WINDOWS(false),
    LINUX(true),
    MAC(true),
    ANDROID(true),
    UNKNOWN_POSIX(true),
    ;

    companion object {
        /**
         * Best-effort detection of an [Os] from a free-form [description] (e.g. the `os.name` JVM system
         * property), matching well-known substrings case-insensitively.
         * @return the detected [Os], or `null` if [description] matches none of the known substrings
         */
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
