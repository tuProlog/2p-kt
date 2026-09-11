package it.unibo.tuprolog.ui.gui.presentation

/**
 * Shared font-size bounds for a Prolog source editor's zoom controls (Ctrl/Cmd +/-/0, Ctrl/Cmd + wheel), so
 * every toolkit (ide-swing's RSyntaxTextArea, ide-web's Ace) agrees on how far zooming is allowed to go,
 * instead of each reimplementing its own (previously drifted: 8-48 vs. 8-40) clamp.
 */
object EditorZoom {
    const val DEFAULT_FONT_SIZE = 14
    const val MIN_FONT_SIZE = 8
    const val MAX_FONT_SIZE = 48

    /** Clamps [fontSize] to the supported [MIN_FONT_SIZE]..[MAX_FONT_SIZE] range. */
    fun clamp(fontSize: Int): Int = fontSize.coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)
}
