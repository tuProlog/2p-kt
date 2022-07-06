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

dependencies {
    implementation(kotlin("bom"))
    api(kotlin("stdlib-jdk8"))
}

java {
    withSourcesJar()
}

tasks.register("jvmTest") {
    group = "verification"
    dependsOn("test")
}

tasks.register("jvmMainClasses") {
    group = "build"
    dependsOn("classes")
}

tasks.register("jvmTestClasses") {
    group = "build"
    dependsOn("testClasses")
}
