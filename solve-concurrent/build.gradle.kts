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
                implementation(project(":dsl-theory"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(project(":test-solve"))
            }
        }
        getByName("jvmMain") {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

tasks.withType<KotlinJvmTest>().configureEach {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}
