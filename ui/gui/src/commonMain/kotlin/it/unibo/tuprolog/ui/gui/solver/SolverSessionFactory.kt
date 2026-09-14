package it.unibo.tuprolog.ui.gui.solver

/** Builds `SolverSession`s for one `SolverProfile` - the profile's own factory function. */
fun interface SolverSessionFactory {
    /** Builds a new session ready to resolve queries against, seeded from [request]. */
    suspend fun create(request: SolverSessionCreationRequest): SolverSession
}
