plugins {
    // Deliberately id(...pluginId) rather than alias(...): kt-mpp's own plugins are all bundled in one jar
    // already on this build's classpath (via the root project's ktMpp.multiProjectHelper), so alias(...)'s
    // version-pinned `id(...) version "..."` form conflicts ("plugin already on the classpath with an unknown
    // version") - id(...pluginId) (no version) resolves against what's already loaded instead.
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.npmPublish
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.multiProjectHelper
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.fatJar
            .get()
            .pluginId,
    )
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
                    api(project(":ide-web"))
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
