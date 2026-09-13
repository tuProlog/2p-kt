package it.unibo.tuprolog.ui.gui.controller

/** How the user resolved a "close a dirty page?" prompt. */
enum class CloseDecision {
    /** Save the document first, then close. */
    SAVE,

    /** Close without saving, discarding pending changes. */
    DISCARD,

    /** Don't close the page after all. */
    CANCEL,
}
