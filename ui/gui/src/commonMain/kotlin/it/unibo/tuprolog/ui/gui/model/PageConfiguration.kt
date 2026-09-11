package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.time.Duration

data class PageConfiguration(
    val solverProfileOverride: SolverProfileId? = null,
    val timeoutOverride: Duration? = null,
    val optionOverrides: Map<String, String> = emptyMap(),
)
