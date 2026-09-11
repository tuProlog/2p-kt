package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.solver.ResolutionSchedulingPolicy
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class WorkspaceConfiguration(
    val defaultSolverProfileId: SolverProfileId,
    val defaultTimeout: Duration = 5.seconds,
    val defaultOptions: Map<String, String> = emptyMap(),
    val schedulingPolicy: ResolutionSchedulingPolicy = ResolutionSchedulingPolicy.PER_PAGE_CONCURRENT,
)
