package it.unibo.tuprolog.ui.gui.solver

import it.unibo.tuprolog.ui.gui.identity.PageId
import kotlin.time.Duration

data class ResolutionRequest(
    val pageId: PageId,
    val query: String,
    val timeout: Duration,
    val options: Map<String, String>,
    val stdin: String,
)
