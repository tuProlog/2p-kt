package it.unibo.tuprolog.ui.gui.solver

/** A single, stateful run of one query, advanced one [ResolutionStep] at a time - the toolkit-neutral
 * equivalent of a Prolog solution iterator. */
interface ResolutionCursor {
    /** Computes one observable resolution step. */
    suspend fun next(): ResolutionStep

    /** Requests cancellation of the underlying computation, where supported. */
    suspend fun cancel()
}
