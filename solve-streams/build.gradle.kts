import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
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

            main.defaultSourceSet {
                dependsOn(commonMain)
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