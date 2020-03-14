plugins {
    id("com.eden.orchidPlugin") version Versions.com_eden_orchidplugin_gradle_plugin
}

dependencies {
    orchidRuntimeOnly("io.github.javaeden.orchid", "OrchidDocs", Versions.com_eden_orchidplugin_gradle_plugin)
    orchidRuntimeOnly("io.github.javaeden.orchid", "OrchidKotlindoc", Versions.com_eden_orchidplugin_gradle_plugin)
    orchidRuntimeOnly("io.github.javaeden.orchid", "OrchidPluginDocs", Versions.com_eden_orchidplugin_gradle_plugin)
    orchidRuntimeOnly("io.github.javaeden.orchid", "OrchidAsciidoc", Versions.com_eden_orchidplugin_gradle_plugin)
    orchidRuntimeOnly("io.github.javaeden.orchid", "OrchidDiagrams", Versions.com_eden_orchidplugin_gradle_plugin)
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx/")
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