package it.unibo.tuprolog.ui.web

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

@Serializable
data class PersistedDocument(
    val displayName: String,
    val text: String,
    val originProviderId: String? = null,
    val originReference: String? = null,
    val dirty: Boolean = false,
    /** Whether this was a page-local scratch buffer rather than a (possibly still-untitled) document. */
    val isScratch: Boolean = false,
    /** The page's last (possibly not yet solved) query text. */
    val query: String = "",
    /** Past queries submitted on this page, oldest first - restores the Up/Down query-history navigation. */
    val queryHistory: List<String> = emptyList(),
    /** Past (concluded) resolutions - restores the Solutions tree's content without re-running anything. */
    val resolutions: List<PersistedResolution> = emptyList(),
)

@Serializable
data class PersistedWorkspace(
    /** The single, page-wide Ace editor zoom level (see AceEditorView) - ide-web has one shared editor, not
     * one per page, unlike ide-swing. */
    val fontSize: Int? = null,
    val selectedIndex: Int = -1,
    val documents: List<PersistedDocument> = emptyList(),
)
