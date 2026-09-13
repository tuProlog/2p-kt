package it.unibo.tuprolog.ui.gui.controller

/** Root of every action a frontend can `dispatch` to a [GuiController] - see [ApplicationAction], [WorkspaceAction],
 * [PageAction], and [DocumentAction] for the actual families. */
sealed interface GuiAction
