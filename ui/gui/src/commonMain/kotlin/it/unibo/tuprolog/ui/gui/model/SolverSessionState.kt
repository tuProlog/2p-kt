package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities

data class SolverSessionState(
    val lifecycle: SolverSessionLifecycle = SolverSessionLifecycle.ABSENT,
    val sessionId: SolverSessionId? = null,
    val profileId: SolverProfileId? = null,
    val loadedDocumentRevision: Long? = null,
    val capabilities: SolverCapabilities = SolverCapabilities.EMPTY,
    val inspection: SolverInspectionSnapshot = SolverInspectionSnapshot(),
    val error: String? = null,
) {
    val isFresh: Boolean get() = lifecycle == SolverSessionLifecycle.FRESH
}
