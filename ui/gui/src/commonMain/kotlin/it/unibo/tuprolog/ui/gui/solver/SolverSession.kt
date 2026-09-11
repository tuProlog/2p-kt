package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot

interface SolverSession {
    val id: SolverSessionId
    val capabilities: SolverCapabilities
    val snapshot: SolverInspectionSnapshot

    suspend fun openResolution(request: ResolutionRequest): ResolutionCursor

    /** Restores a pristine solver state. Implementations may rebuild internally. */
    suspend fun reset(): SolverInspectionSnapshot

    suspend fun close()
}
