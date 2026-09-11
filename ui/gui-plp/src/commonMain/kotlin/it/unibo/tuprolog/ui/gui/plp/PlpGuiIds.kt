package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.ExtensionId
import it.unibo.tuprolog.ui.gui.identity.FeatureId

object PlpGuiIds {
    val EXTENSION: ExtensionId = ExtensionId("plp")
    val SOLUTION_DETAILS: FeatureId = FeatureId("plp.solution-details")
    val BDD_INSPECTOR: FeatureId = FeatureId("plp.bdd-inspector")
    val COPY_BDD_DOT: CommandId = CommandId("plp.copy-bdd-dot")
}
