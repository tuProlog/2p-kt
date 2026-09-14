package it.unibo.tuprolog.ui.gui.application

import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import kotlinx.coroutines.CoroutineScope

/**
 * Lifecycle wrapper around the toolkit-neutral controller.
 *
 * The hosting frontend owns the parent [CoroutineScope], so application shutdown cannot leak work into a process-global
 * scope. Solver profiles and semantic extensions are composed explicitly at the executable's composition root.
 */
class GuiApplication internal constructor(
    val controller: GuiController,
) {
    suspend fun start(): GuiApplication {
        controller.dispatch(ApplicationAction.Start)
        return this
    }

    suspend fun close() {
        controller.shutdown()
    }
}
