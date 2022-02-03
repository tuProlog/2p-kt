import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    `kotlin-jvm-js`
}

val jvmStackSize: String by project
val jvmMaxHeapSize: String by project

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                implementation(project(":dsl-theory"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":test-solve"))
            }
        }
    }
}

tasks.withType<KotlinJvmTest> {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}
