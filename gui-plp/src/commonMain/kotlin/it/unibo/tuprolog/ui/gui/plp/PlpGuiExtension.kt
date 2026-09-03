package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.ui.gui.controller.GuiEffect
import it.unibo.tuprolog.ui.gui.extension.CommandDescriptor
import it.unibo.tuprolog.ui.gui.extension.ExtensionActionHandler
import it.unibo.tuprolog.ui.gui.extension.ExtensionCommandResult
import it.unibo.tuprolog.ui.gui.extension.GuiContributions
import it.unibo.tuprolog.ui.gui.extension.GuiExtension
import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.EffectId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.presentation.FeatureDescriptor
import it.unibo.tuprolog.ui.gui.presentation.FeaturePlacement
import it.unibo.tuprolog.ui.gui.presentation.SemanticRegion
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverProfile

object PlpGuiIds {
    val EXTENSION: ExtensionId = ExtensionId("plp")
    val SOLUTION_DETAILS: FeatureId = FeatureId("plp.solution-details")
    val BDD_INSPECTOR: FeatureId = FeatureId("plp.bdd-inspector")
    val COPY_BDD_DOT: CommandId = CommandId("plp.copy-bdd-dot")
}

/**
 * Common PLP contribution. The actual ProbLog [solverProfile] is supplied by a solver-adapter module;
 * this module contains no Swing, Compose, browser, Graphviz-Java, or JVM-only type.
 */
class PlpGuiExtension(
    solverProfile: SolverProfile,
) : GuiExtension {
    init {
        require(SolverCapabilities.PROBABILISTIC_SOLUTIONS in solverProfile.capabilities) {
            "PLP solver profile must advertise probabilistic solutions"
        }
    }

    override val id: ExtensionId = PlpGuiIds.EXTENSION

    override val contributions: GuiContributions =
        GuiContributions(
            solverProfiles = listOf(solverProfile),
            features =
                listOf(
                    FeatureDescriptor(
                        id = PlpGuiIds.SOLUTION_DETAILS,
                        displayName = "Probability",
                        placement = FeaturePlacement(SemanticRegion.RESULTS, priority = 100),
                        requiredCapabilities = setOf(SolverCapabilities.PROBABILISTIC_SOLUTIONS),
                    ),
                    FeatureDescriptor(
                        id = PlpGuiIds.BDD_INSPECTOR,
                        displayName = "Binary decision diagram",
                        placement = FeaturePlacement(SemanticRegion.INSPECTOR, priority = 100),
                        requiredCapabilities = setOf(SolverCapabilities.BDD_PRESENTATION),
                    ),
                ),
            commands =
                listOf(
                    CommandDescriptor(
                        id = PlpGuiIds.COPY_BDD_DOT,
                        displayName = "Copy BDD as DOT",
                        featureId = PlpGuiIds.BDD_INSPECTOR,
                    ),
                ),
            actionHandler =
                ExtensionActionHandler { context ->
                    if (context.commandId != PlpGuiIds.COPY_BDD_DOT) {
                        ExtensionCommandResult()
                    } else {
                        val dot =
                            context.payload[PlpFeatureKeys.BDD_DOT]
                                ?: (
                                    context.page.features[PlpGuiIds.BDD_INSPECTOR]
                                        ?.values
                                        ?.get(
                                            PlpFeatureKeys.BDD_DOT,
                                        ) as? it.unibo.tuprolog.ui.gui.model.FeatureValue.Text
                                )?.value
                                ?: error("No BDD DOT representation is available for page ${context.pageId}")
                        ExtensionCommandResult(
                            effects =
                                listOf(
                                    GuiEffect.CopyTextToClipboard(
                                        id = EffectId("plp-copy-${context.pageId.value}"),
                                        text = dot,
                                    ),
                                ),
                        )
                    }
                },
        )
}
