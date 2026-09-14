package it.unibo.tuprolog.ui.gui.controller

/** The toolkit-neutral core of the application: consumes [GuiAction]s and exposes the resulting [GuiModel]. Every
 * frontend (Swing, web, ...) talks to exactly one of these and never touches solver/state internals directly. */
interface GuiController : GuiModel {
    /** Applies one action, updating [GuiModel.state] and/or emitting [GuiModel.events]/[GuiModel.effects]. */
    suspend fun dispatch(action: GuiAction)

    /** Releases every resource this controller holds (solver sessions, coroutines); unusable afterwards. */
    suspend fun shutdown()
}
