
plugins {
    kotlin("multiplatform") version "1.3.40"
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version "0.9.18"
//    id("com.moowork.node") version "1.3.1"
}

// apply next commands to all subprojects
subprojects {

    group = "it.unibo.tuprolog"
    version = "1.0-SNAPSHOT"

    // ** NOTE ** legacy plugin application, because the new "plugins" block is not available inside "subprojects" scope yet
    // when it will be available it should be moved here
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
//    apply(plugin = "com.moowork.node")

    // projects dependencies repositories
    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/dokka")
        maven("https://jitpack.io")
        mavenLocal()
    }

    // Common kotlin multiplatform configuration for sub-projects
    kotlin {

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-common"))
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                }
            }

            // Default source set for JVM-specific sources and dependencies:
            jvm {
                compilations["main"].defaultSourceSet {
                    dependencies {
                        implementation(kotlin("stdlib-jdk8"))
                    }
                }

                mavenPublication {
                    artifactId = project.name + "-jvm"
                }

                // JVM-specific tests and their dependencies:
                compilations["test"].defaultSourceSet {
                    dependencies {
                        implementation(kotlin("test-junit"))
                    }
                }
            }
        }
    }
}
