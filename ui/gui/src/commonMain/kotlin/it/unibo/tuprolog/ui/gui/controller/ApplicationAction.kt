package it.unibo.tuprolog.ui.gui.controller

sealed interface ApplicationAction : GuiAction {
    data object Start : ApplicationAction

    data object RequestExit : ApplicationAction

    data object ExitConfirmed : ApplicationAction

    data object ExitCancelled : ApplicationAction
}
