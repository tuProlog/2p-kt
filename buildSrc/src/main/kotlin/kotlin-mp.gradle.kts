plugins {
    kotlin("multiplatform")
}

val mochaTimeout: String by project
val ktCompilerArgsJvm: String by project
val ktCompilerArgs: String by project

val disableJvm: Boolean = project.findProperty("ktTargetJvmDisable")?.toString()?.toBoolean() ?: false
val disableJs: Boolean = project.findProperty("ktTargetJsDisable")?.toString()?.toBoolean() ?: false

kotlin {
    if (!disableJvm) {
        jvm {
            withJava()
            compilations.all {
                kotlinOptions {
                    freeCompilerArgs = freeCompilerArgs + ktCompilerArgsJvm.split(";")
                }
            }
        }
    }

    if (!disableJs) {
        js {
            useCommonJs()
            compilations.all {
                kotlinOptions {
                    main = "noCall"
                }
            }
            nodejs {
                testTask {
                    useMocha {
                        timeout = mochaTimeout
                    }
                }
            }
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("bom"))
                api(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        if (!disableJvm) {
            val jvmMain by getting {
                dependencies {
                    api(kotlin("stdlib-jdk8"))
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
        }
        if (!disableJs) {
            val jsMain by getting {
                dependencies {
                    api(kotlin("stdlib-js"))
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                freeCompilerArgs = freeCompilerArgs + ktCompilerArgs.split(";")
            }
        }
    }
}
