package it.unibo.tuprolog.ui.gui.controller

/** Actions about the application as a whole (startup, exit), rather than any one page or document. */
sealed interface ApplicationAction : GuiAction {
    /** Dispatched once at startup, before the first page is created. */
    data object Start : ApplicationAction

    /** The user asked to quit; may still be vetoed if dirty documents need confirming. */
    data object RequestExit : ApplicationAction

    /** Exit was confirmed (no dirty documents, or the user chose to discard/save them) - shutdown proceeds. */
    data object ExitConfirmed : ApplicationAction

    /** The user backed out of exiting; the application keeps running. */
    data object ExitCancelled : ApplicationAction
}
