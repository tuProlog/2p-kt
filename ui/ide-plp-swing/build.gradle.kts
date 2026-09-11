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

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.plp.Main")
}

registerVerifyFatJarTask(
    entryPoint = "it.unibo.tuprolog.ui.swing.plp.Main",
    expectSuccess = false,
    expectedFailureFragment = "Cannot show the Swing IDE in a headless environment",
)
