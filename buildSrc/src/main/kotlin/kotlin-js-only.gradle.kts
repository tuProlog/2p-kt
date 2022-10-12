import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("js")
}

val mochaTimeout: String by project
val ktCompilerArgs: String by project

kotlin {
    js {
        useCommonJs()
        compilations.all {
            kotlinOptions {
                main = "noCall"
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = mochaTimeout
                }
            }
        }
    }
}

dependencies {
    implementation(kotlin("bom"))
    api(kotlin("stdlib-js"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        freeCompilerArgs = ktCompilerArgs.split(";").toList()
    }
}

tasks.register("jsTest") {
    group = "verification"
    dependsOn("test")
}

tasks.register("jsMainClasses") {
    group = "build"
    dependsOn("mainClasses")
}

tasks.register("jsTestClasses") {
    group = "build"
    dependsOn("testClasses")
}
