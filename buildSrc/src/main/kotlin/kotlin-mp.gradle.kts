plugins {
    kotlin("multiplatform")
}

val mochaTimeout: String by project
val ktCompilerArgsJvm: String by project
val ktCompilerArgs: String by project

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + ktCompilerArgsJvm.split(";")
            }
        }
    }

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

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                freeCompilerArgs = freeCompilerArgs + ktCompilerArgs.split(";")
            }
        }
    }
}
