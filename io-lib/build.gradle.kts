import jsDependenciesFrom
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import packageJsonFile
import java.net.ServerSocket

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
                implementation(project(":parser-impl"))
                implementation(libs.okio)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-solve"))
                implementation(project(":solve-prolog"))
                implementation(libs.okio.fakefilesystem)
            }
        }

        named("jsMain") {
            dependencies {
                // Okio has no synchronous file system for JS; NodeJsFileSystem covers local files on Node.
                implementation(libs.okio.nodefilesystem)
                // Okio is not an HTTP client: remote consult/1 still needs a synchronous HTTP call on JS,
                // which the (synchronous) Solver API requires. Kept until the Solver gets an async story.
                jsDependenciesFrom(project.packageJsonFile) {
                    implementation(npm("sync-request"))
                }
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

// A free port per build, so that a leftover server (e.g. from another daemon) can't make the tests fail.
val mockServicePort = ServerSocket(0).use { it.localPort }

mockService {
    port = mockServicePort

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

tasks.withType<Test>().configureEach { environment("MOCK_SERVICE_PORT", mockServicePort) }
tasks.withType<KotlinJsTest>().configureEach { environment("MOCK_SERVICE_PORT", mockServicePort.toString()) }
