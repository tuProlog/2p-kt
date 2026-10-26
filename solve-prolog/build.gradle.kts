import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

val jvmStackSize: String = findProperty("jvmStackSize")?.toString() ?: "256m"
val jvmMaxHeapSize: String = findProperty("jvmMaxHeapSize")?.toString() ?: "512m"

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":solve"))
                implementation(project(":dsl-theory"))
            }
        }

        commonTest {
            dependencies {
                api(project(":test-solve"))
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

tasks.withType<KotlinJvmTest>().configureEach {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}
