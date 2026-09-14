package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.gui.controller.GuiAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.GuiEffect
import it.unibo.tuprolog.ui.gui.controller.GuiEvent
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.ApplicationState
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.model.WorkspaceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal object InertGuiController : GuiController {
    override val state: StateFlow<GuiState> =
        MutableStateFlow(
            GuiState(
                ApplicationState(),
                WorkspaceState(
                    configuration = WorkspaceConfiguration(defaultSolverProfileId = SolverProfileId("test")),
                ),
            ),
        )
    override val events: SharedFlow<GuiEvent> = MutableSharedFlow()
    override val effects: Flow<GuiEffect> = MutableSharedFlow()

    override suspend fun dispatch(action: GuiAction) = Unit

    override suspend fun shutdown() = Unit
}
