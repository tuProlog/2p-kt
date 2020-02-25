import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
            }
        }

        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                api(project(":solve-test"))
            }
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
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

//tasks.withType<KotlinJvmTest> {
//    jvmArgs("-Xmx1G", "-Xss256M")
//    println("${name}.jvmArgs: $jvmArgs")
//    println("${name}.allJvmArgs: $allJvmArgs")
//}