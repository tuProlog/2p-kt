import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
    `mock-service`
}

apply<MockServicePlugin>()

kotlin {
    sourceSets {

        val commonMain by getting {
            dependencies {
                api(project(":utils"))
            }
        }
        val commonTest by getting
        val jsTest by getting
        val jsMain by getting {
            dependencies {
                implementation(npm("sync-request", libs.versions.npm.syncRequest.get()))
            }
        }

        val testCompileTask = tasks.withType<Kotlin2JsCompile>().matching { "Test" in it.name }.single()
        tasks.maybeCreate("copyTaskJsResources", Copy::class.java).run {
            listOf(commonMain, jsMain, commonTest, jsTest).forEach { sourceSet ->
                from(sourceSet.resources.files)
            }
            into(testCompileTask.outputFileProperty.get().parentFile.parentFile)
            testCompileTask.dependsOn(this)
        }
    }
}

fun Project.getCommonResource(name: String): File {
    val file = file("src/commonTest/resources/it/unibo/tuprolog/utils/io").resolve(name)
    if (!file.exists()) {
        throw IllegalStateException("Missing resource: $file")
    }
    return file
}

mockService {
    port = 8081

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
}

tasks.matching { it.name in setOf("jvmTest", "jsNodeTest") }.configureEach {
    dependsOn(mockService.startMockTask)
    finalizedBy(mockService.stopMockTask)
}

packageJson {
    dependencies = mutableMapOf(
        "sync-request" to libs.versions.npm.syncRequest.get(),
    )
}
