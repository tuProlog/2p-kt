

plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
    id(libs.plugins.ktMpp.npmPublish.get().pluginId)
    id(libs.plugins.ktMpp.multiProjectHelper.get().pluginId)
    id(libs.plugins.shadowJar.get().pluginId)
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
                    ktProjects.except("test-solve", thisProject).forEach {
                        api(it)
                    }

                }
            }
            getByName("jvmMain") {
                dependencies {
                    jvmProjects.except("examples").forEach {
                        api(it)
                    }
                }
            }
        }
    }
}

val supportedPlatforms by extra { listOf("win", "linux", "mac", "mac-aarch64") }

shadowJar(classifier = "full")

for (platform in supportedPlatforms) {
    shadowJar(platform = platform, classifier = "full")
}
