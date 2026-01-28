@file:Suppress("UnstableApiUsage")

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity") version "4.3.2"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.1.7"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

fun tag(
    format: String,
    vararg properties: String,
): String = format.format(*properties.map { System.getProperty(it) ?: error("Missing property $it") }.toTypedArray())

val ci = !System.getenv("CI").isNullOrBlank()
val ciTag = if (ci) "CI" else "Local"
val osTag = "OS: " + tag("%s (%s) v. %s", "os.name", "os.arch", "os.version")
val jvmTag = "JVM: " + tag("%s v. %s", "java.vm.name", "java.vm.version")
val whenTag = "When: ${ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)}"

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        tag(ciTag)
        tag(osTag)
        tag(jvmTag)
        tag(whenTag)
        uploadInBackground = !ci
        publishing.onlyIf { it.buildResult.failures.isNotEmpty() }
        buildScanPublished {
            if (ci) {
                println("::error title=Gradle scan for $osTag, $jvmTag, $whenTag::$buildScanUri")
            }
        }
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    createHooks(true)
}

rootProject.name = "2p"

// include(":documentation")
include(":utils")
include(":core")
include(":unify")
include(":theory")
include(":datalog")
include(":bdd")
include(":dsl-core")
include(":dsl-unify")
include(":dsl-theory")
include(":dsl-solve")
include(":solve")
include(":solve-classic")
include(":solve-streams")
include(":solve-concurrent")
include(":test-solve")
include(":test-dsl")
include(":parser-core")
include(":parser-jvm")
include(":parser-js")
include(":parser-theory")
include(":solve-plp")
include(":solve-problog")
include(":serialize-core")
include(":serialize-theory")
include(":repl")
include(":oop-lib")
include(":io-lib")
include(":ide-plp")
include(":ide")
include(":examples")
include(":full")
