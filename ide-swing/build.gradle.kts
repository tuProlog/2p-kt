plugins {
    application
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":gui"))
    implementation(project(":parser-impl"))
    implementation(project(":parser-theory"))
    implementation(project(":io-lib"))
    implementation(project(":oop-lib"))
    implementation(project(":solve-classic"))
    implementation(libs.rsyntaxtextarea)
    implementation(libs.autocomplete)
    implementation(libs.rstaui)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.SwingMainKt")
}

val copyIdeLogo =
    tasks.register<Copy>("copyIdeLogo") {
        from(rootProject.layout.projectDirectory.file(".img/logo.png"))
        into(layout.buildDirectory.dir("generated-resources/logo"))
    }

tasks.processResources {
    dependsOn(copyIdeLogo)
    from(layout.buildDirectory.dir("generated-resources/logo"))
}
