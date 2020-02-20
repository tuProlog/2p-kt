import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project

// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
            }
        }

        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":solve-test"))
            }
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            with(main) {
                kotlinOptions {
                    jvmTarget = "1.6"
                    freeCompilerArgs = ktFreeCompilerArgsJvm.split(';').toList()
                }

                defaultSourceSet {
                    dependsOn(commonMain)
                    dependencies {
                        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
                    }
                }
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }

        js {

            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
                }
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }

    }
}

tasks.withType<KotlinJvmTest> {
    jvmArgs("-Xmx1G", "-Xss256M")
    println("${name}.jvmArgs: $jvmArgs")
    println("${name}.allJvmArgs: $allJvmArgs")
}