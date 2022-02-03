import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("js")
}

val mochaTimeout: String by project
val ktCompilerArgs: String by project

kotlin {
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        freeCompilerArgs = ktCompilerArgs.split(";").toList()
    }
}
