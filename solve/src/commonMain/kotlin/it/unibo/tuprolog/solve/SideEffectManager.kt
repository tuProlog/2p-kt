package it.unibo.tuprolog.solve

import kotlin.js.JsName

/**
 * An interface representing a manager for flow modifications that can occur to Prolog resolution process
 *
 * @author Enrico
 */
interface SideEffectManager {

    /** A function to execute cut, and return a new [SideEffectManager] with executed cut */
    @JsName("cut")
    fun cut(): SideEffectManager

}
