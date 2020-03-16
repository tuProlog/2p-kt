plugins {
    id("com.eden.orchidPlugin") version Versions.com_eden_orchidplugin_gradle_plugin
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(Libs.plantuml)
    }
}

dependencies {
    orchidRuntimeOnly(Libs.orchiddocs)
    orchidRuntimeOnly(Libs.orchidkotlindoc)
    orchidRuntimeOnly(Libs.orchidplugindocs)
    orchidRuntimeOnly(Libs.orchidasciidoc)
    orchidRuntimeOnly(Libs.orchiddiagrams)
}

repositories {
    mavenCentral()
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
}

configurations {
    val orchidRuntimeOnly by getting {
        resolutionStrategy {
            force(Libs.plantuml)
        }
    }
}