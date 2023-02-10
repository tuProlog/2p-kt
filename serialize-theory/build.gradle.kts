plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":theory"))
                api(project(":serialize-core"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":solve"))
            }
        }

        val jvmMain by getting {
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
    tasks.create("print${it.capitalize()}", JavaExec::class.java) {
        group = "application"
        dependsOn("jvmTestClasses")
        classpath = files(
            kotlin.jvm().compilations.getByName("test").output,
            kotlin.jvm().compilations.getByName("test").compileDependencyFiles
        )
        standardInput = System.`in`
        main = "${it.toUpperCase()}PrinterKt"
    }
}
