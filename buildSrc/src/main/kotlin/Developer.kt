import Organization.Companion.getOrg
import org.gradle.api.Project

data class Developer(val name: String, val url: String?, val email: String?, val org: Organization?) {

    fun toPerson() = mutableMapOf<String, Any?>(
        "name" to name,
        "url" to url,
        "email" to email,
    )

    companion object {
        fun Project.getDev(key: String): Developer {
            val name = property("${key}Name")?.toString() ?: error("Missing property ${key}Name")
            val url = findProperty("${key}Url")?.toString()
            val email = findProperty("${key}Email")?.toString()
            val orgKey = findProperty("${key}Org")?.toString()
            val org = orgKey?.let { getOrg(it) }
            return Developer(name, url, email, org)
        }

        fun Project.getAllDevs(): Set<Developer> =
            properties.keys.asSequence()
                .filter { it.startsWith("developer") && it.endsWith("Name") }
                .map { it.replace("Name", "") }
                .distinct()
                .map { getDev(it) }
                .toSet()
    }
}
