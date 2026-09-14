package it.unibo.tuprolog.ui.gui.model

/** The part of [GuiState] describing the application's own lifecycle, independent of any page/workspace. */
data class ApplicationState(
    val started: Boolean = false,
    val exitRequested: Boolean = false,
    val metadata: ApplicationMetadata = ApplicationMetadata(),
)
