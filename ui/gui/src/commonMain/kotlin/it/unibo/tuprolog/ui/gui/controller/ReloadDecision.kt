package it.unibo.tuprolog.ui.gui.controller

/** How the user resolved a "reload this document from disk, discarding unsaved changes?" prompt. */
enum class ReloadDecision {
    /** Reload from disk, discarding pending in-memory changes. */
    DISCARD_CHANGES,

    /** Don't reload; keep the in-memory (dirty) content. */
    CANCEL,
}
