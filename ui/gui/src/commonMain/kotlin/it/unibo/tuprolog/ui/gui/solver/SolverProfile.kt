package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId

data class SolverProfile(
    val id: SolverProfileId,
    val displayName: String,
    val capabilities: SolverCapabilities,
    val factory: SolverSessionFactory,
    val defaultOptions: Map<String, String> = emptyMap(),
)
