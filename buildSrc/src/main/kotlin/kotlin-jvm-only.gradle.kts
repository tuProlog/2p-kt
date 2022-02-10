import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
}

val ktCompilerArgsJvm: String by project
val ktCompilerArgs: String by project

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        freeCompilerArgs = listOf(ktCompilerArgs, ktCompilerArgsJvm).flatMap { it.split(";") }
    }
}

java {
    withSourcesJar()
}

tasks.register("jvmTest") {
    group = "verification"
    dependsOn("test")
}
