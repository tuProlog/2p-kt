package it.unibo.tuprolog.ui.gui.model

/** One of a page's lower-tab panels, e.g. for tracking per-panel unread-changes badges. */
enum class PanelId {
    SOLUTIONS,
    STDIN,
    STDOUT,
    STDERR,
    WARNINGS,
    DIAGNOSTICS,
    OPERATORS,
    FLAGS,
    LIBRARIES,
    STATIC_KB,
    DYNAMIC_KB,
}
