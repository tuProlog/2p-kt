import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.streams.asSequence

object NodeVersions {
    private fun getLatestVersionOfWithMajor(major: String): String {
        val url = URL("https://nodejs.org/dist/latest-v$major.x/")
        return findVersion(url) ?: error("Cannot retrieve last version of node $major")
    }

    private fun getLatestVersion(): String {
        val url = URL("https://nodejs.org/dist/latest/")
        return findVersion(url) ?: error("Cannot retrieve last version of node")
    }

    private val FILE_REGEX = "\"node-v(\\d+).(\\d+).(\\d+).*?\"".toRegex()

    private val MAJOR_REGEX = "\\d+".toRegex()

    private val FULL_VERSION_REGEX = "\\d+.\\d+.\\d+".toRegex()

    private val LATEST_VERSION_REGEX = "(?:v?)(\\d+)-latest|latest-(?:v?)(\\d+)".toRegex(RegexOption.IGNORE_CASE)

    private fun findVersion(url: URL): String? {
        BufferedReader(InputStreamReader(url.openStream())).use { reader ->
            return reader.lines().asSequence()
                .map { FILE_REGEX.find(it) }
                .filterNotNull()
                .filter { it.groups.size >= 3 }
                .map { it.groupValues.subList(1, 4).joinToString(".") }
                .firstOrNull()
        }
    }

    private val VERSIONS_CACHE = mutableMapOf<String, String>()

    fun latest(major: String = "latest"): String =
        VERSIONS_CACHE.computeIfAbsent(major) {
            when {
                it.equals("latest", ignoreCase = true) -> getLatestVersion()
                MAJOR_REGEX.matches(it) -> getLatestVersionOfWithMajor(it)
                else -> error("Major number expected, provided: $it")
            }
        }

    fun toFullVersion(version: String): String = when {
        version.equals("latest", ignoreCase = true) -> latest()
        FULL_VERSION_REGEX.matches(version) -> version
        else -> {
            val match = LATEST_VERSION_REGEX.matchEntire(version) ?: error("Invalid version string: $version")
            val major = match.groupValues.let { it.subList(1, it.size) }.first { !it.isEmpty() }
            latest(major)
        }
    }
}


