import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

val jvmStackSize: String = findProperty("jvmStackSize")?.toString() ?: "256m"
val jvmMaxHeapSize: String = findProperty("jvmMaxHeapSize")?.toString() ?: "512m"

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
            }
        }

        commonTest {
            dependencies {
                api(project(":test-solve"))
            }
        }
    }
}

tasks.withType<KotlinJvmTest>().configureEach {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}
