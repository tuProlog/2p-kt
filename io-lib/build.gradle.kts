import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":parser-theory"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
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
                implementation(npm("sync-request", "6.1.0"))
            }
        }

        val jsTest by getting

        val testCompileTask = tasks.withType<Kotlin2JsCompile>().matching { "Test" in it.name }.single()
        tasks.maybeCreate("copyTaskJsResources", Copy::class.java).run {
            listOf(commonMain, jsMain, commonTest, jsTest).forEach { sourceSet ->
                from(sourceSet.resources.files)
            }
            into(testCompileTask.outputFile.parentFile.parentFile)
            testCompileTask.dependsOn(this)
        }
    }
}
