@file:JvmName("Resources")

package it.unibo.tuprolog.ui.gui

import javafx.scene.image.Image

val JAVA_KEYWORDS_LIGHT: String by lazy {
    TuPrologIDE::class.java.getResource("java-keywords-light.css").toExternalForm()
}

val JAVA_KEYWORDS_DARK: String by lazy {
    TuPrologIDE::class.java.getResource("java-keywords-dark.css").toExternalForm()
}

val LIGHT_CODE_AREA: String by lazy {
    TuPrologIDE::class.java.getResource("light-code-area.css").toExternalForm()
}

val DARK_CODE_AREA: String by lazy {
    TuPrologIDE::class.java.getResource("dark-code-area.css").toExternalForm()
}

// TODO update image with new logo
val TUPROLOG_LOGO: Image by lazy {
    TuPrologIDE::class.java.getResource("2p-logo.png").let { Image(it.openStream()) }
}
