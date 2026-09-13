package it.unibo.tuprolog.ui.gui.extension

import it.unibo.tuprolog.ui.gui.controller.GuiEffect
import it.unibo.tuprolog.ui.gui.controller.GuiEvent
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.FeatureValue

/** What an [ExtensionActionHandler] produced for one command: any feature-state updates, plus events/effects to
 * feed back through the same channels the core controller uses. */
data class ExtensionCommandResult(
    val featureUpdates: Map<FeatureId, Map<String, FeatureValue>> = emptyMap(),
    val events: List<GuiEvent> = emptyList(),
    val effects: List<GuiEffect> = emptyList(),
)
