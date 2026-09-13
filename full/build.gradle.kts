plugins {
    alias(libs.plugins.ktMpp.mavenPublish)
    alias(libs.plugins.ktMpp.npmPublish)
    alias(libs.plugins.ktMpp.multiProjectHelper)
    alias(libs.plugins.ktMpp.fatJar)
}

val thisProject = project.name

multiProjectHelper {
    kotlin {
        js {
            binaries.library()
        }
        sourceSets {
            commonMain {
                dependencies {
                    ktProjects.except("test-solve", thisProject, rootProject.name).forEach {
                        api(it)
                        logger.lifecycle("${project.path} depends on ${it.path}")
                    }
                }
            }
            getByName("jvmMain") {
                dependencies {
                    jvmProjects.except("examples").forEach {
                        api(it)
                        logger.lifecycle("${project.path} depends on ${it.path}")
                    }
                }
            }
            getByName("jvmTest") {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
            getByName("jsMain") {
                dependencies {
                    api(":ide-web")
                    logger.lifecycle("${project.path} depends on :ide-web")
                }
            }
        }
    }
}

registerVerifyFatJarTask(
    entryPoint = "it.unibo.tuprolog.PrologCLI",
    launchArgs = listOf("solve", "true."),
    expectedOutputFragment = "yes",
)
