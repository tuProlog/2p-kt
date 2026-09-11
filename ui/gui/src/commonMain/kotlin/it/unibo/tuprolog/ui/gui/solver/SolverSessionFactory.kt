package it.unibo.tuprolog.ui.gui.solver

fun interface SolverSessionFactory {
    suspend fun create(request: SolverSessionCreationRequest): SolverSession
}
