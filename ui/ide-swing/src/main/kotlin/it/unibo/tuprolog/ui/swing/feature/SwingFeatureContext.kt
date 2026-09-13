package it.unibo.tuprolog.ui.swing.feature

import it.unibo.tuprolog.ui.gui.controller.GuiAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.swing.dispatch
import kotlinx.coroutines.CoroutineScope

/** Services exposed to a toolkit-specific feature renderer without leaking them into the common extension API. */
data class SwingFeatureContext(
    val controller: GuiController,
    val scope: CoroutineScope,
) {
    fun dispatch(action: GuiAction) {
        scope.dispatch { controller.dispatch(action) }
    }
}
