package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.presentation.FeatureDescriptor
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

data class GuiContributions(
    val solverProfiles: List<SolverProfile> = emptyList(),
    val features: List<FeatureDescriptor> = emptyList(),
    val commands: List<CommandDescriptor> = emptyList(),
    val actionHandler: ExtensionActionHandler? = null,
)
