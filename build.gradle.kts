import io.github.gciatto.kt.mpp.ProjectExtensions.jvmProjects
import io.github.gciatto.kt.mpp.ProjectExtensions.ktProjects

plugins {
    id("io.github.gciatto.kt-mpp-pp")
    id("org.danilopianini.git-sensitive-semantic-versioning")
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
