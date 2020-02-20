import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

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
            dependencies {
                implementation(project(":solve-test"))
            }
        }

        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
                }
            }

        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
                }
            }
        }
    }
}

tasks.withType<KotlinJvmTest> {
    jvmArgs("-Xmx1G", "-Xss256M")
    println("${name}.jvmArgs: $jvmArgs")
    println("${name}.allJvmArgs: $allJvmArgs")
}