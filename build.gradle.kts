import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
//import io.github.gciatto.kt.mpp.ProjectConfiguration.configureUploadToGithub
//import io.github.gciatto.kt.mpp.ProjectExtensions.jsProjects
//import io.github.gciatto.kt.mpp.ProjectExtensions.jvmProjects
//import io.github.gciatto.kt.mpp.ProjectExtensions.ktProjects
//import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.time.Duration
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.dokka)
    alias(libs.plugins.shadowJar)
    `publish-on-maven`
    `publish-on-npm`
    `print-versions`
}

group = "io.github.gciatto"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    assignGitSemanticVersion()
}

logger.log(LogLevel.LIFECYCLE, "${rootProject.name} version: $version")

allprojects {
    repositories {
        mavenCentral()
    }
}

jvmVersion(libs.versions.jvm)

nodeVersion(default = libs.versions.node, override = project.findProperty("nodeVersion"))

packageJson {
    version = project.npmCompliantVersion
    dependencies {
        "kotlin" to libs.versions.kotlin.get()
    }
}

tasks.withType<DokkaTask>().matching { "Html" in it.name }.all {
    val dokkaHtml = this
    tasks.create<Jar>("dokkaHtmlJar") {
        group = "documentation"
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
        dependsOn(dokkaHtml)
    }
}

group = "it.unibo.tuprolog"

subprojects {
    group = rootProject.group
    version = rootProject.version
}

val otherProjects = subprojects("documentation")
val jsProjects = subprojects("parser-js")
val jvmProjects = subprojects("examples", "ide", "ide-plp")
val ktProjects = allprojects.toSet() - (otherProjects + jsProjects + jvmProjects)

//kotlinMultiplatform {
//    projectLongName.set("2P-Kt")
//    preventPublishingOfRootProject.set(true)
//    jvmOnlyProjects("examples", "ide", "parser-jvm", "ide-plp")
//    jsOnlyProjects("parser-js")
//    otherProjects("documentation")
//    ktProjects(allOtherSubprojects)
//    developer("Giovanni Ciatto", "giovanni.ciatto@unibo.it", "http://about.me/gciatto")
//    developer("Enrico Siboni", "enrico.siboni3@studio.unibo.it")
//    developer("Paolo Verdini", "paolo.verdini@studio.unibo.it")
//    developer("Manuel Bonarrigo", "manuel.bonarrigo@studio.unibo.it")
//    developer("Sofia Montebugnoli", "sofia.montebugnoli2@studio.unibo.it")
//    developer("Davide Greco", "davide.greco4@studio.unibo.it")
//    developer("Silvia Lanzoni", "silvia.lanzoni5@studio.unibo.it")
//    developer("Lorenzo Rizzato", "lorenzo.rizzato@studio.unibo.it")
//    developer("Jason Dellaluce", "jason.dellaluce@studio.unibo.it")
//}

// kotlin {
//     sourceSets {
//         val commonMain by getting {
//             dependencies {
//                 ktProjects.except("test-solve") {
//                     api(project(path))
//                 }
//             }
//
//         }
//         val jvmMain by getting {
//             dependencies {
//                 jvmProjects.except("examples") {
//                     api(project(path))
//                 }
//             }
//         }
//     }
// }

val shadowJar by tasks.getting(ShadowJar::class) {
    dependsOn("jvmMainClasses")
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("full")
    from(kotlin.jvm().compilations.getByName("main").output)
    from(files("${rootProject.projectDir}/LICENSE"))
}
