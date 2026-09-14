package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import kotlin.time.Duration

/** A page's own overrides on top of the workspace-wide `WorkspaceConfiguration` - `null`/absent fields fall
 * back to the workspace default (see `WorkspaceConfiguration.resolve`). */
data class PageConfiguration(
    val solverProfileOverride: SolverProfileId? = null,
    val timeoutOverride: Duration? = null,
    val optionOverrides: Map<String, String> = emptyMap(),
)
