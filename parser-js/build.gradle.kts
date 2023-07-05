plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

kotlin {
    sourceSets {
        main {
            dependencies {
                api(kotlin("stdlib-js"))
                api(npm("@tuprolog/parser-utils", libs.versions.npm.tuprolog.parserUtils.get()))
            }
        }
        test {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.create<Copy>("jar") {
    group = "build"
    tasks.assemble.orNull?.dependsOn(this)
    dependsOn("jsJar")
    destinationDir = buildDir.resolve("libs")
    from(destinationDir) {
        rename("(.*)\\.klib", "$1.jar")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications.withType<MavenPublication> {
        val jarFile = buildDir.resolve("libs").resolve("${project.name}-${project.version}.jar")
        artifact(jarFile)
        tasks.withType<AbstractPublishToMaven>()
            .matching { it.name.contains(name, ignoreCase = true) }
            .configureEach { dependsOn("jar") }
    }
}
