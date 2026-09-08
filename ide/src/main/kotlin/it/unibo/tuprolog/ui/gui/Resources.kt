/**
 * Bundled UI resources shipped in this module's classpath, exposed as ready-to-use JavaFX values so host
 * applications don't need to know their resource paths. The CSS URLs are meant to be added to
 * [TuPrologIDEBuilder.stylesheets]; the default light theme ([JAVA_KEYWORDS_LIGHT] + [LIGHT_CODE_AREA]) is
 * [TuPrologIDEBuilder]'s out-of-the-box choice, [JAVA_KEYWORDS_DARK]/[DARK_CODE_AREA] are the dark-theme alternative.
 */
@file:JvmName("Resources")

package it.unibo.tuprolog.ui.gui

import javafx.scene.image.Image

/** External-form URL of the light-theme keyword/syntax-highlighting stylesheet
 * (matches [SyntaxColoring]'s style classes). */
val JAVA_KEYWORDS_LIGHT: String by lazy {
    TuPrologIDEApplication::class.java.getResource("java-keywords-light.css").toExternalForm()
}

/** External-form URL of the dark-theme keyword/syntax-highlighting stylesheet. */
val JAVA_KEYWORDS_DARK: String by lazy {
    TuPrologIDEApplication::class.java.getResource("java-keywords-dark.css").toExternalForm()
}

/** External-form URL of the light-theme code-editor stylesheet (fonts, background, line numbers, ...). */
val LIGHT_CODE_AREA: String by lazy {
    TuPrologIDEApplication::class.java.getResource("light-code-area.css").toExternalForm()
}

/** External-form URL of the dark-theme code-editor stylesheet. */
val DARK_CODE_AREA: String by lazy {
    TuPrologIDEApplication::class.java.getResource("dark-code-area.css").toExternalForm()
}

/** The tuProlog logo image, used as the default [TuPrologIDEBuilder.icon] and in the default "about" dialog. */
val TUPROLOG_LOGO: Image by lazy {
    TuPrologIDEApplication::class.java.getResource("2p-logo.png").let { Image(it.openStream()) }
}
