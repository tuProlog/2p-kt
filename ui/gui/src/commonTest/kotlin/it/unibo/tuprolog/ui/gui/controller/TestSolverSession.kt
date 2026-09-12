package it.unibo.tuprolog.ui.gui.controller

import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.solver.ResolutionCursor
import it.unibo.tuprolog.ui.gui.solver.ResolutionRequest
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.gui.solver.SolverSession

internal class TestSolverSession(
    override val id: SolverSessionId,
) : SolverSession {
    override val capabilities =
        SolverCapabilities(
            setOf(
                SolverCapabilities.CANCELLATION,
                SolverCapabilities.FLAGS_INSPECTION,
            ),
        )

    override val snapshot =
        SolverInspectionSnapshot(
            flags = listOf(FlagPresentation("test", "true")),
        )

    override suspend fun openResolution(request: ResolutionRequest): ResolutionCursor = TestResolutionCursor(request)

    override suspend fun reset(): SolverInspectionSnapshot = snapshot

    override suspend fun close() = Unit
}
