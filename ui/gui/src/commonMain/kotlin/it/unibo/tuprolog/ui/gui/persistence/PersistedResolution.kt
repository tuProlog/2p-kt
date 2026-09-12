package it.unibo.tuprolog.ui.gui.persistence

import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlinx.serialization.Serializable

@Serializable
data class PersistedBinding(
    val variable: String,
    val value: String,
)

/** Mirrors [SolutionPresentation]'s shape for JSON persistence; converted back via [toSolutionPresentation]. */
@Serializable
sealed interface PersistedSolution {
    @Serializable
    data class Yes(
        val query: String,
        val bindings: List<PersistedBinding> = emptyList(),
        val solvedQuery: String? = null,
        val metadata: Map<String, String> = emptyMap(),
    ) : PersistedSolution

    @Serializable
    data class No(
        val query: String,
    ) : PersistedSolution

    @Serializable
    data class Halt(
        val query: String,
        val message: String,
        val logicStackTrace: List<String> = emptyList(),
        val isTimeout: Boolean = false,
    ) : PersistedSolution
}

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

private fun SolutionPresentation.toPersisted(): PersistedSolution =
    when (this) {
        is SolutionPresentation.Yes ->
            PersistedSolution.Yes(
                query = query,
                bindings = bindings.map { PersistedBinding(it.variable, it.value) },
                solvedQuery = solvedQuery,
                metadata = metadata,
            )
        is SolutionPresentation.No -> PersistedSolution.No(query)
        is SolutionPresentation.Halt -> PersistedSolution.Halt(query, message, logicStackTrace, isTimeout)
    }

/** [ResolutionStatus.COMPLETED] is the safe fallback for a status name this build no longer recognizes. */
fun PersistedResolution.toResolutionHistoryEntry(): ResolutionHistoryEntry =
    ResolutionHistoryEntry(
        query = query,
        solutions = solutions.map { it.toSolutionPresentation() },
        terminalStatus =
            runCatching { ResolutionStatus.valueOf(terminalStatus) }.getOrDefault(ResolutionStatus.COMPLETED),
        error = error,
    )

private fun PersistedSolution.toSolutionPresentation(): SolutionPresentation =
    when (this) {
        is PersistedSolution.Yes ->
            SolutionPresentation.Yes(
                query = query,
                bindings = bindings.map { BindingPresentation(it.variable, it.value) },
                solvedQuery = solvedQuery,
                metadata = metadata,
            )
        is PersistedSolution.No -> SolutionPresentation.No(query)
        is PersistedSolution.Halt -> SolutionPresentation.Halt(query, message, logicStackTrace, isTimeout)
    }
