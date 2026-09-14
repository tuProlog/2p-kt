package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.presentation.FeatureDescriptor
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

/** Everything one [GuiExtension] adds to the application: solver profiles, feature tabs/panels, commands, and
 * the handler that runs those commands. */
data class GuiContributions(
    val solverProfiles: List<SolverProfile> = emptyList(),
    val features: List<FeatureDescriptor> = emptyList(),
    val commands: List<CommandDescriptor> = emptyList(),
    val actionHandler: ExtensionActionHandler? = null,
)
