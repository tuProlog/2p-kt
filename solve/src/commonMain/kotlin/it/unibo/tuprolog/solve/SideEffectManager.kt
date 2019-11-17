package it.unibo.tuprolog.solve

/**
 * An interface representing a manager for flow modifications that can occur to Prolog resolution process
 *
 * @author Enrico
 */
interface SideEffectManager {

    /** A function to execute cut, and return a new [SideEffectManager] with executed cut */
    fun cut(): SideEffectManager

}
