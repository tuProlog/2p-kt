package it.unibo.tuprolog.ui.gui.persistence

import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlinx.serialization.Serializable

/** Mirrors [SolutionPresentation]'s shape for JSON persistence; converted back via [toSolutionPresentation]. */
@Serializable
sealed interface PersistedSolution {
    /** A successful solution, persisted with its already-formatted solved query and bindings. */
    @Serializable
    data class Yes(
        val query: String,
        val bindings: List<PersistedBinding> = emptyList(),
        val solvedQuery: String? = null,
        val metadata: Map<String, String> = emptyMap(),
    ) : PersistedSolution

    /** A failed solution. */
    @Serializable
    data class No(
        val query: String,
    ) : PersistedSolution

    /** An aborted solution, persisted with its error message and stack trace. */
    @Serializable
    data class Halt(
        val query: String,
        val message: String,
        val logicStackTrace: List<String> = emptyList(),
        val isTimeout: Boolean = false,
    ) : PersistedSolution
}

/** Converts to the persisted, JSON-serializable shape. */
internal fun SolutionPresentation.toPersisted(): PersistedSolution =
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

/** Restores the runtime presentation from its persisted shape. */
internal fun PersistedSolution.toSolutionPresentation(): SolutionPresentation =
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
