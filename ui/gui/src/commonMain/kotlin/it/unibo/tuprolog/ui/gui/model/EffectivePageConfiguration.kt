package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.time.Duration

data class EffectivePageConfiguration(
    val solverProfileId: SolverProfileId,
    val timeout: Duration,
    val options: Map<String, String>,
)

fun WorkspaceConfiguration.resolve(page: PageConfiguration): EffectivePageConfiguration =
    EffectivePageConfiguration(
        solverProfileId = page.solverProfileOverride ?: defaultSolverProfileId,
        timeout = page.timeoutOverride ?: defaultTimeout,
        options = defaultOptions + page.optionOverrides,
    )
