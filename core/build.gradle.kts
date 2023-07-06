import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

val tuPrologPackage get() = rootProject.group.toString()
val tuPrologPackageDir get() = tuPrologPackage.replace('.', File.separatorChar)

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.ktMath)
                api(project(":utils"))
            }

            val infoKtFile = kotlin.srcDirs.first().absoluteFile.resolve("$tuPrologPackageDir/Info.kt")

            val createInfoKt by tasks.creating {
                doLast {
                    infoKtFile.writeText(
                        """
                        |package $tuPrologPackage
                        |
                        |object Info {
                        |    const val VERSION = "${rootProject.version}"
                        |    val PLATFORM: Platform by lazy { currentPlatform() }
                        |    val OS: Os by lazy { currentOs() }
                        |}
                        |
                        """.trimMargin()
                    )
                }
                outputs.file(infoKtFile)
            }

            tasks.matching { it.name.endsWith("sourcesJar", ignoreCase = true) }
                .configureEach { dependsOn(createInfoKt) }
            tasks.withType<Detekt> { dependsOn(createInfoKt) }
            tasks.withType<AbstractDokkaTask>().configureEach { dependsOn(createInfoKt) }
            tasks.withType<BaseKtLintCheckTask> { dependsOn(createInfoKt) }
            tasks.withType<KotlinCompile<*>> { dependsOn(createInfoKt) }
        }
    }
}
