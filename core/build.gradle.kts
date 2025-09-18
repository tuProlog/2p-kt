import io.gitlab.arturbosch.detekt.Detekt
import org.danilopianini.gradle.gitsemver.SemanticVersion
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

val tuPrologPackage: String
    get() = group.toString()

val tuPrologPackageDir: String
    get() = tuPrologPackage.replace('.', File.separatorChar)

val infoKtFileContent: String
    get() =
        """|package $tuPrologPackage
        |
        |object Info {
        |    const val VERSION = "$version"
        |    val PLATFORM: Platform by lazy { currentPlatform() }
        |    val OS: Os by lazy { currentOs() }
        |}
        |
        """.trimMargin()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.ktMath)
                api(project(":utils"))
            }

            val infoKtFile =
                kotlin.srcDirs
                    .first()
                    .absoluteFile
                    .resolve("$tuPrologPackageDir/Info.kt")

            val createInfoKt by tasks.registering {
                doFirst {
                    val version = version.toString()
                    val rootVersion = rootProject.version.toString()
                    require(version matches SemanticVersion.semVerRegex) {
                        "Invalid version of project ${project.name}: $version"
                    }
                    require(version == rootVersion) {
                        "Version of project ${project.name} ($version) does not match " +
                            "root project's one ($rootVersion)"
                    }
                }
                doLast {
                    infoKtFile.writeText(infoKtFileContent)
                }
                outputs.file(infoKtFile)
                outputs.upToDateWhen { infoKtFile.exists() && infoKtFile.readText().contains(version.toString()) }
            }

            tasks
                .matching { it.name.endsWith("sourcesJar", ignoreCase = true) }
                .configureEach { dependsOn(createInfoKt) }
            tasks.withType<Detekt>().configureEach { dependsOn(createInfoKt) }
            tasks.withType<AbstractDokkaTask>().configureEach { dependsOn(createInfoKt) }
            tasks.withType<BaseKtLintCheckTask>().configureEach { dependsOn(createInfoKt) }
            tasks.withType<KotlinCompilationTask<*>>().configureEach { dependsOn(createInfoKt) }
        }
    }
}
