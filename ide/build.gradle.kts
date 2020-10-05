import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project

plugins {
    application
    kotlin("jvm")
    id("org.openjfx.javafxplugin") version Versions.org_openjfx_javafxplugin_gradle_plugin
}

javafx {
    version = Versions.org_openjfx
    modules = listOf("javafx.controls")
}

application {
    mainClassName = "it.unibo.tuprolog.ui.gui.TuPrologFx"
}

dependencies {
    api(project(":solve-classic"))
    api(project(":parser-theory"))
    api(kotlin("stdlib-jdk8"))

    implementation(Libs.richtextfx)
    testImplementation(kotlin("test-junit"))
}


configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.$javaVersion"
        freeCompilerArgs = ktFreeCompilerArgsJvm.split(";").toList()
    }
}
