plugins {
    application
}

dependencies {
    api(project(":gui"))
    implementation(project(":parser-theory"))
    implementation(project(":solve-classic"))
    implementation(libs.kotlinx.coroutines.core)
}

application {
    mainClass.set("it.unibo.tuprolog.ui.swing.SwingMainKt")
}
