package it.unibo.tuprolog.ui.gui.solver

interface ResolutionCursor {
    /** Computes one observable resolution step. */
    suspend fun next(): ResolutionStep

    /** Requests cancellation of the underlying computation, where supported. */
    suspend fun cancel()
}
