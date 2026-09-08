package it.unibo.tuprolog.solve.sideffects

import kotlin.js.JsName

/**
 * An interface representing a manager for flow modifications that can occur to Prolog resolution process --
 * currently, just the effect of cutting (`!/0`) choice points.
 *
 * A [SideEffectManager] instance is optionally attached to a
 * [it.unibo.tuprolog.solve.primitive.Solve.Response] (see
 * [it.unibo.tuprolog.solve.primitive.Solve.Request.replyWith]), letting a primitive signal that resolution should
 * behave as if a cut had been executed, without the primitive needing to know the concrete resolution strategy's
 * representation of choice points.
 *
 * @author Enrico
 */
interface SideEffectManager {
    /** Signals a cut, returning a new [SideEffectManager] reflecting it. */
    @JsName("cut")
    fun cut(): SideEffectManager
}
