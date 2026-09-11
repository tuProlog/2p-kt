plugins {
    application
    alias(libs.plugins.kotlin.serialization)
    id(
        libs.plugins.ktMpp.fatJar
            .get()
            .pluginId,
    )
}

dependencies {
    api(project(":gui"))
    implementation(project(":gui-prolog"))
    implementation(project(":solve-classic"))
    implementation(libs.rsyntaxtextarea)
    implementation(libs.autocomplete)
    implementation(libs.rstaui)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.clikt)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.Main")
}

registerSwingE2eTestSourceSet(libs.assertj.swing)

// detektSwingE2eTest is an EXPERIMENTAL type-resolution task the kt-mpp bug-finder plugin doesn't wire into
// detektAll/check by default for a custom source set; static analysis needs no display, so it's safe to fold in.
tasks.named("detektAll") { dependsOn("detektSwingE2eTest") }

registerVerifyFatJarTask(
    entryPoint = "it.unibo.tuprolog.ui.swing.Main",
    expectSuccess = false,
    expectedFailureFragment = "Cannot show the Swing IDE in a headless environment",
)

val copyIdeLogo =
    tasks.register<Copy>("copyIdeLogo") {
        from(rootProject.layout.projectDirectory.file(".img/logo.png"))
        into(layout.buildDirectory.dir("generated-resources/logo"))
    }

tasks.processResources {
    dependsOn(copyIdeLogo)
    from(layout.buildDirectory.dir("generated-resources/logo"))
}
