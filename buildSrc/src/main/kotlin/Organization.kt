import org.gradle.api.Project

data class Organization(val name: String, val url: String) {
    companion object {
        fun Project.getOrg(key: String): Organization {
            val name = property("${key}Name")?.toString() ?: error("Missing property ${key}Name")
            val url = findProperty("${key}Url")?.toString() ?: error("Missing property ${key}Url")
            return Organization(name, url)
        }
    }
}
