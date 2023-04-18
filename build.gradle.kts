plugins {
    alias(libs.plugins.gitSemVer)
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `print-versions`
}

group = "io.github.gciatto"

gitSemVer {
    excludeLightweightTags()
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
            version.set(project.npmCompliantVersion)
        }
    }
}
