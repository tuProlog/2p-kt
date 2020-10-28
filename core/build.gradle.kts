import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

val tuPrologPackage get() = rootProject.group.toString()
val tuPrologPackageDir get() = tuPrologPackage.replace('.', File.separatorChar)

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Libs.kt_math)
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
                        |    
                        |    val PLATFORM: Platform by lazy {  currentPlatform() }
                        |}
                        |""".trimMargin()
                    )
                }
                outputs.file(infoKtFile)
            }

            tasks.withType<KotlinCompile<*>>().forEach {
                it.dependsOn(createInfoKt)
                it.inputs.file(infoKtFile)
            }
        }
    }
}
