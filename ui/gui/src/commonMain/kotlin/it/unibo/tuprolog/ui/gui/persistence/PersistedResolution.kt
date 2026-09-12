package it.unibo.tuprolog.ui.gui.persistence

import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import kotlinx.serialization.Serializable

/** Mirrors [ResolutionHistoryEntry]'s shape for JSON persistence; converted back via [toResolutionHistoryEntry]. */
@Serializable
data class PersistedResolution(
    val query: String,
    val solutions: List<PersistedSolution>,
    val terminalStatus: String,
    val error: String? = null,
)

fun ResolutionHistoryEntry.toPersisted(): PersistedResolution =
    PersistedResolution(
        query = query,
        solutions = solutions.map { it.toPersisted() },
        terminalStatus = terminalStatus.name,
        error = error,
    )

/** [ResolutionStatus.COMPLETED] is the safe fallback for a status name this build no longer recognizes. */
fun PersistedResolution.toResolutionHistoryEntry(): ResolutionHistoryEntry =
    ResolutionHistoryEntry(
        query = query,
        solutions = solutions.map { it.toSolutionPresentation() },
        terminalStatus =
            runCatching { ResolutionStatus.valueOf(terminalStatus) }.getOrDefault(ResolutionStatus.COMPLETED),
        error = error,
    )
