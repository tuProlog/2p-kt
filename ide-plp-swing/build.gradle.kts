plugins {
    application
}

dependencies {
    implementation(project(":gui"))
    implementation(project(":gui-solve"))
    implementation(project(":gui-plp"))
    implementation(project(":ide-swing"))
    implementation(project(":solve-problog"))
    implementation(libs.clikt)

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.plp.PlpSwingMainKt")
}
