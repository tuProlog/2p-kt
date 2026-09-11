package it.unibo.tuprolog.ui.gui.model

data class ApplicationState(
    val started: Boolean = false,
    val exitRequested: Boolean = false,
    val metadata: ApplicationMetadata = ApplicationMetadata(),
)
