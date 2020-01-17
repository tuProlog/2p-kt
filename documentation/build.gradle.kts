plugins {
    id("com.eden.orchidPlugin") version "0.18.1"
}

dependencies {
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidDocs:0.18.1")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidKotlindoc:0.18.1")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidPluginDocs:0.18.1")
//    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidGitlab:0.18.1")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidDiagrams:0.18.1")
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx/")
}

fun getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}

// env ORG_GRADLE_PROJECT_orchidBaseUrl
val orchidBaseUrl = getPropertyOrWarnForAbsence("orchidBaseUrl")

orchid {
    theme = "Editorial"
    baseUrl = orchidBaseUrl
    version = rootProject.version.toString()
    args = listOf("--experimentalSourceDoc")
//    gitlabToken = gitlabApiKey
}