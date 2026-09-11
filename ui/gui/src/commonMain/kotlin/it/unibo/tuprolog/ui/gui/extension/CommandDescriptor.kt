package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.FeatureId

data class CommandDescriptor(
    val id: CommandId,
    val displayName: String,
    val featureId: FeatureId? = null,
)
