import org.gradle.internal.extensions.stdlib.capitalized
import java.util.Locale

plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":theory"))
                api(project(":serialize-core"))
            }
        }

        commonTest {
            dependencies {
                api(project(":solve"))
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(libs.jackson.core)
                implementation(libs.jackson.xml)
                implementation(libs.jackson.yaml)
                implementation(libs.jackson.jsr310)
            }
        }
    }
}

listOf("yaml", "json").forEach {
    tasks.register("print${it.capitalized()}", JavaExec::class.java) {
        group = "application"
        dependsOn(tasks.named("jvmTestClasses"))
        classpath =
            files(
                kotlin
                    .jvm()
                    .compilations
                    .getByName("test")
                    .output,
                kotlin
                    .jvm()
                    .compilations
                    .getByName("test")
                    .compileDependencyFiles,
            )
        standardInput = System.`in`
        mainClass.set("${it.uppercase(Locale.getDefault())}PrinterKt")
    }
}
