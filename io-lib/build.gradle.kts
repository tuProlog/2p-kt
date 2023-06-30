plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
    alias(libs.plugins.gradleMockService)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":parser-theory"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":test-solve"))
                implementation(project(":solve-classic"))
                implementation(project(":solve-streams"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("sync-request", libs.versions.npm.syncRequest.get()))
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
