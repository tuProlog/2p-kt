import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

val jvmStackSize: String by project
val jvmMaxHeapSize: String by project

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
    }
}

tasks.withType<KotlinJvmTest> {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}
