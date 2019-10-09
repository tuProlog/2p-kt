package it.unibo.tuprolog.solve

/**
 * An interface representing a manager for flow modifications that can occur to Prolog resolution process
 *
 * @author Enrico
 */
interface SideEffectManager {

    /** A function to execute cut, and return a new [SideEffectManager] that knows what to do */
    fun cut(): SideEffectManager

    /** Method that queries if a Cut should be executed in a Primitive implementation body */
    fun shouldCutExecuteInPrimitive(): Boolean

}
