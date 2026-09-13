package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot

/** A live solver instance backing one page, built by a `SolverSessionFactory` from a `SolverProfile`. */
interface SolverSession {
    /** Uniquely identifies this session while it's alive. */
    val id: SolverSessionId

    /** What this particular session supports (may narrow its profile's declared capabilities). */
    val capabilities: SolverCapabilities

    /** The current inspectable state (operators, flags, libraries, knowledge base) of the underlying solver. */
    val snapshot: SolverInspectionSnapshot

    /** Starts resolving [request]'s query, returning a cursor to step through its solutions. */
    suspend fun openResolution(request: ResolutionRequest): ResolutionCursor

    /** Restores a pristine solver state. Implementations may rebuild internally. */
    suspend fun reset(): SolverInspectionSnapshot

    /** Releases whatever resources this session holds; the session is unusable afterwards. */
    suspend fun close()
}
