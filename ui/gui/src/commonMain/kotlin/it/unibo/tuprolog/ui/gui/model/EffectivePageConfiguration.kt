package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.time.Duration

/** A page's actually-effective solver profile/timeout/options, after applying its own overrides on top of the
 * workspace-wide defaults - see [resolve]. */
data class EffectivePageConfiguration(
    val solverProfileId: SolverProfileId,
    val timeout: Duration,
    val options: Map<String, String>,
)

/** Merges [page]'s per-page overrides on top of this workspace-wide configuration. */
fun WorkspaceConfiguration.resolve(page: PageConfiguration): EffectivePageConfiguration =
    EffectivePageConfiguration(
        solverProfileId = page.solverProfileOverride ?: defaultSolverProfileId,
        timeout = page.timeoutOverride ?: defaultTimeout,
        options = defaultOptions + page.optionOverrides,
    )
