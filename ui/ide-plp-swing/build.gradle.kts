import org.gradle.jvm.toolchain.JavaLanguageVersion

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
    implementation(libs.graphviz)
    // Pure-JVM DOT rendering engine for graphviz-java (no native `dot` binary needed - see GraphvizBddRenderer).
    runtimeOnly(libs.graphviz.js.engine)

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

// graphviz-java's GraalVM-JS engine (see GraphvizSwingBddGraphRenderer) uses a Truffle version whose
// `sun.misc.Unsafe.ensureClassInitialized` call breaks on very new JDKs; pin `test` to one it's known to work
// on, same as registerSwingE2eTestSourceSet already does for the swingE2eTest source set below.
tasks.withType<Test>().configureEach {
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        },
    )
}
