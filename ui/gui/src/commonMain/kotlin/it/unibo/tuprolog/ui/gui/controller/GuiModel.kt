package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.model.GuiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface GuiModel {
    val state: StateFlow<GuiState>
    val events: SharedFlow<GuiEvent>

    /** Lossless, ordered, single-consumer requests to the hosting platform. */
    val effects: Flow<GuiEffect>
}
