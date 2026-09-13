package it.unibo.tuprolog.ui.gui.presentation

/** One known operator (name, priority, and fixity specifier like "xfx"/"fy"), toolkit-neutral and ready to
 * display or feed into syntax highlighting. */
data class OperatorPresentation(
    val name: String,
    val priority: Int,
    val specifier: String,
)
