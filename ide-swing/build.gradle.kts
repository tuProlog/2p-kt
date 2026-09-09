plugins {
    application
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

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.SwingMainKt")
}
