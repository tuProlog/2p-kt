package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.identity.CommandId
import it.unibo.tuprolog.ui.gui.identity.FeatureId

/** A command an extension contributes, invokable from the UI (e.g. a button) and routed to its [ExtensionActionHandler]. */
data class CommandDescriptor(
    val id: CommandId,
    val displayName: String,
    val featureId: FeatureId? = null,
)
