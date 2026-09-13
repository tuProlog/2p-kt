package it.unibo.tuprolog.ui.gui.model

/** The entire observable state of a `GuiController` at one point in time - what `GuiModel.state` holds. */
data class GuiState(
    val application: ApplicationState,
    val workspace: WorkspaceState,
)
