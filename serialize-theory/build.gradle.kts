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
                implementation("com.fasterxml.jackson.core:jackson-core:_")
                implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:_")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:_")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:_")
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
