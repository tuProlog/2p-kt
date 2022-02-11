plugins {
    alias(libs.plugins.gitSemVer)
    `kotlin-jvm-js`
    `kotlin-joint-doc`
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
    apply(plugin = "kotlin-style-checker")
    apply(plugin = "kotlin-bug-finder")
}

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
