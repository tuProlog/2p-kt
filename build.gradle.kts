//import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.dokka)
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

//packageJson {
//    version = project.npmCompliantVersion
//    dependencies {
//        "kotlin" to libs.versions.kotlin.get()
//    }
//}
//
//tasks.withType<DokkaTask>().matching { "Html" in it.name }.all {
//    val dokkaHtml = this
//    tasks.create<Jar>("dokkaHtmlJar") {
//        group = "documentation"
//        archiveClassifier.set("javadoc")
//        from(dokkaHtml.outputDirectory)
//        dependsOn(dokkaHtml)
//    }
//}

group = "it.unibo.tuprolog"

jvmVersion(libs.versions.jvm)
nodeVersion(libs.versions.node, rootProject.findProperty("nodeVersion"))

subprojects {
    group = rootProject.group
    version = rootProject.version

    afterEvaluate {
        jvmVersion(libs.versions.jvm)
        nodeVersion(libs.versions.node, project.findProperty("nodeVersion"))
        packageJson {
            version = project.npmCompliantVersion
            dependencies {
                "kotlin" to libs.versions.kotlin.get()
            }
        }
    }
}
