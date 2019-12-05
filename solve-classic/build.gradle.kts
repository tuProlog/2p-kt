// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":solve-test"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation("it.unibo.alice.tuprolog:tuprolog:3.3.0")
                }
            }
        }

    }
}
