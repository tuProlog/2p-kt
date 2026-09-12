plugins {
    application
    id(
        libs.plugins.ktMpp.fatJar
            .get()
            .pluginId,
    )
}

dependencies {
    implementation(project(":gui"))
    implementation(project(":gui-prolog"))
    implementation(project(":gui-plp"))
    implementation(project(":ide-swing"))
    implementation(project(":solve-problog"))
    implementation(libs.clikt)
    // Pure-JVM diagram rendering (Smetana layout engine - see PlantUmlSwingBddGraphRenderer): no native
    // Graphviz install and no GraalVM needed, unlike the graphviz-java dependency this used to carry.
    implementation(libs.plantuml)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.plp.Main")
}

registerSwingE2eTestSourceSet(libs.assertj.swing)

// detektSwingE2eTest is an EXPERIMENTAL type-resolution task the kt-mpp bug-finder plugin doesn't wire into
// detektAll/check by default for a custom source set; static analysis needs no display, so it's safe to fold in.
tasks.named("detektAll") { dependsOn("detektSwingE2eTest") }

registerVerifyFatJarTask(
    entryPoint = "it.unibo.tuprolog.ui.swing.plp.Main",
    expectSuccess = false,
    expectedFailureFragment = "Cannot show the Swing IDE in a headless environment",
)
