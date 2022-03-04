import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

val tuPrologPackage get() = rootProject.group.toString()
val tuPrologPackageDir get() = tuPrologPackage.replace('.', File.separatorChar)

kotlin {
    sourceSets {
        val commonMain by getting {
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
                        |}
                        |""".trimMargin()
                    )
                }
                outputs.file(infoKtFile)
            }

            val addDependecyAction: (Task) -> Unit = {
                it.dependsOn(createInfoKt)
                it.inputs.file(infoKtFile)
            }

            tasks.withType<KotlinCompile<*>>().forEach(addDependecyAction)
            // tasks.withType<GenerateReportsTask>().forEach(addDependecyAction)
        }
    }
}
