import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

val jvmStackSize: String by project
val jvmMaxHeapSize: String by project

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                implementation(project(":dsl-theory"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            }
        }
        val commonTest by getting {
            dependencies {
                api(project(":test-solve"))
            }
        }
        // val jvmTest by getting {
        //     dependencies {
        //         implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
        //     }
        // }
    }
}

tasks.withType<KotlinJvmTest> {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}
