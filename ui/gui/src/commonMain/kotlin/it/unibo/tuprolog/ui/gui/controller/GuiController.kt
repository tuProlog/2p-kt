package it.unibo.tuprolog.ui.gui.controller

interface GuiController : GuiModel {
    suspend fun dispatch(action: GuiAction)

    suspend fun shutdown()
}
