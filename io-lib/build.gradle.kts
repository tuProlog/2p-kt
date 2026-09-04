plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    alias(libs.plugins.gradleMockService)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":solve"))
                api(project(":parser-theory"))
                implementation(libs.okio)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-solve"))
                implementation(project(":solve-classic"))
                implementation(project(":solve-streams"))
                implementation(libs.okio.fakefilesystem)
            }
        }

        named("jsMain") {
            dependencies {
                // Okio has no synchronous file system for JS; NodeJsFileSystem covers local files on Node.
                implementation(libs.okio.nodefilesystem)
                // Okio is not an HTTP client: remote consult/1 still needs a synchronous HTTP call on JS,
                // which the (synchronous) Solver API requires. Kept until the Solver gets an async story.
                implementation(
                    npm(
                        "sync-request",
                        libs.versions.npm.syncRequest
                            .get(),
                    ),
                )
            }
        }
    }
}

fun Project.getCommonResource(name: String): File {
    val file = file("src/commonTest/resources/it/unibo/tuprolog/solve/libs/io").resolve(name)
    if (!file.exists()) {
        throw IllegalStateException("Missing resource: $file")
    }
    return file
}

mockService {
    port = 8080

    val parents = getCommonResource("Parents.pl")
    val parentsWrong = getCommonResource("WrongParents.pl")
    val random = getCommonResource("random.bin")

    routes {
        get("/hello") { it.result("hello") }
        get("/parents.pl") { it.result(parents.inputStream()) }
        get("/parents-wrong.pl") { it.result(parentsWrong.inputStream()) }
        get("/random.bin") {
            it.contentType("application/octet-stream")
            it.result(random.inputStream())
        }
    }

    wrapTasks("jvmTest", "jsNodeTest")
}
