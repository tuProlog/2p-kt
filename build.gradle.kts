import io.github.gciatto.kt.mpp.ProjectExtensions.jsProjects
import io.github.gciatto.kt.mpp.ProjectExtensions.jvmProjects
import io.github.gciatto.kt.mpp.ProjectExtensions.ktProjects
import java.time.Duration

plugins {
    id("io.github.gciatto.kt-mpp-pp")
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("de.marcphilipp.nexus-publish")
}

group = "it.unibo.tuprolog"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    developmentCounterLength.set(2) // How many digits after `dev`
    version = computeGitSemVer() // THIS IS MANDATORY, AND MUST BE LAST IN THIS BLOCK!
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}

println("${rootProject.name} version: $version")

kotlinMultiplatform {
    projectLongName.set("2P-Kt")
    preventPublishingOfRootProject.set(true)
    jvmOnlyProjects("examples", "ide", "parser-jvm")
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
