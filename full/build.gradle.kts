

plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
    id("com.github.johnrengelman.shadow")
}

val thisProject = project.name

kotlin {
    js {
        binaries.library()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                subprojects(ktProjects, except = setOf("test-solve", thisProject)) {
                    api(this)
                }
            }
        }
        val jvmMain by getting {
            dependencies {
                subprojects(jvmProjects, except = "examples") {
                    api(this)
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
