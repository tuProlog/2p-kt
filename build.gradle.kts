import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.github.gciatto.kt.mpp.ProjectConfiguration.configureUploadToGithub
import io.github.gciatto.kt.mpp.ProjectExtensions.jsProjects
import io.github.gciatto.kt.mpp.ProjectExtensions.jvmProjects
import io.github.gciatto.kt.mpp.ProjectExtensions.ktProjects
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.time.Duration

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktMppPP)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.shadowJar)
}

group = "it.unibo.tuprolog"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    fullHash.set(false) // set to true if you want to use the full git hash
    maxVersionLength.set(Int.MAX_VALUE) // Useful to limit the maximum version length, e.g. Gradle Plugins have a limit on 20
    developmentCounterLength.set(2) // How many digits after `dev`
    enforceSemanticVersioning.set(true) // Whether the plugin should stop if the resulting version is not a valid SemVer, or just warn
    // The separator for the pre-release block.
    // Changing it to something else than "+" may result in non-SemVer compatible versions
    preReleaseSeparator.set("-")
    // The separator for the build metadata block.
    // Some systems (notably, the Gradle plugin portal) do not support versions with a "+" symbol.
    // In these cases, changing it to "-" is appropriate.
    buildMetadataSeparator.set("+")
    distanceCounterRadix.set(36) // The radix for the commit-distance counter. Must be in the 2-36 range.
    // A prefix on tags that should be ignored when computing the Semantic Version.
    // Many project are versioned with tags named "vX.Y.Z", de-facto building valid SemVer versions but for the leading "v".
    // If it is the case for some project, setting this property to "v" would make these tags readable as SemVer tags.
    versionPrefix.set("")
    assignGitSemanticVersion()
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}

println("${rootProject.name} version: $version")

kotlinMultiplatform {
    projectLongName.set("2P-Kt")
    preventPublishingOfRootProject.set(true)
    jvmOnlyProjects("examples", "ide", "parser-jvm", "ide-plp")
    jsOnlyProjects("parser-js")
    otherProjects("documentation")
    ktProjects(allOtherSubprojects)
    developer("Giovanni Ciatto", "giovanni.ciatto@unibo.it", "http://about.me/gciatto")
    developer("Enrico Siboni", "enrico.siboni3@studio.unibo.it")
    developer("Paolo Verdini", "paolo.verdini@studio.unibo.it")
    developer("Manuel Bonarrigo", "manuel.bonarrigo@studio.unibo.it")
    developer("Sofia Montebugnoli", "sofia.montebugnoli2@studio.unibo.it")
    developer("Davide Greco", "davide.greco4@studio.unibo.it")
    developer("Silvia Lanzoni", "silvia.lanzoni5@studio.unibo.it")
    developer("Lorenzo Rizzato", "lorenzo.rizzato@studio.unibo.it")
    developer("Jason Dellaluce", "jason.dellaluce@studio.unibo.it")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            ktProjects.filter { it.name !in setOf("test-solve") }.forEach {
                dependencies {
                    api(project(":${it.name}"))
                }
            }
        }
        val jvmMain by getting {
            jvmProjects.filter { it.name !in setOf("examples") }.forEach {
                dependencies {
                    api(project(":${it.name}"))
                }
            }
        }
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    dependsOn("jvmMainClasses")
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("full")
    from(kotlin.jvm().compilations.getByName("main").output)
    from(files("${rootProject.projectDir}/LICENSE"))
}

configureUploadToGithub(shadowJar)

(ktProjects + jvmProjects + jsProjects + rootProject).forEach {
    it.apply(plugin = "de.marcphilipp.nexus-publish")
    it.nexusPublishing {
        repositories {
            sonatype {
                username.set(project.property("mavenUsername").toString())
                password.set(project.property("mavenPassword").toString())
            }
        }
        clientTimeout.set(Duration.ofMinutes(10))
    }
}

(ktProjects + jvmProjects + rootProject).forEach {
    it.tasks.withType<KotlinJvmCompile> {
        val compatibility = kotlinOptions.jvmTarget
        it.tasks.withType<JavaCompile> {
            sourceCompatibility = compatibility
            targetCompatibility = compatibility
        }
    }
}
