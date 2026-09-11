package it.unibo.tuprolog.ui.gui.application

import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.model.ApplicationMetadata
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

data class GuiApplicationConfiguration(
    val metadata: ApplicationMetadata,
    val workspace: WorkspaceConfiguration,
    val solverProfiles: List<SolverProfile>,
    val extensions: List<GuiExtension>,
)
