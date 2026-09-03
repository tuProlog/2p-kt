plugins {
    application
}

dependencies {
    implementation(project(":gui"))
    implementation(project(":gui-plp"))
    implementation(project(":ide-swing"))
    implementation(project(":solve-problog"))
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.plp.PlpSwingMainKt")
}
