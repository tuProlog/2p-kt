package it.unibo.tuprolog.ui.gui.model

import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.ResolutionSessionId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.identity.SolverSessionId
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolverInspectionSnapshot
import it.unibo.tuprolog.ui.gui.presentation.WarningPresentation
import it.unibo.tuprolog.ui.gui.solver.ResolutionSchedulingPolicy
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ApplicationMetadata(
    val productName: String = "2P-Kt",
    val version: String = "development",
    val homepage: String = "https://github.com/tuProlog/2p-kt",
)

data class ApplicationState(
    val started: Boolean = false,
    val exitRequested: Boolean = false,
    val metadata: ApplicationMetadata = ApplicationMetadata(),
)

data class DocumentOrigin(
    /** Identifies the frontend/platform persistence provider, not a JVM class. */
    val providerId: String,
    /** Opaque to gui. It can represent a path, browser handle, URI, or database key. */
    val opaqueReference: String,
    val displayName: String,
) {
    init {
        require(providerId.isNotBlank()) { "providerId cannot be blank" }
        require(opaqueReference.isNotBlank()) { "opaqueReference cannot be blank" }
        require(displayName.isNotBlank()) { "displayName cannot be blank" }
    }
}

data class DocumentState(
    val id: DocumentId,
    val displayName: String,
    val text: String = "",
    val origin: DocumentOrigin? = null,
    val revision: Long = 0,
    val persistedRevision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
        require(persistedRevision >= 0) { "persistedRevision must be non-negative" }
        require(persistedRevision <= revision) { "persistedRevision cannot exceed revision" }
    }

    val isDirty: Boolean get() = revision != persistedRevision
}

sealed interface PageContent {
    data class DocumentReference(
        val documentId: DocumentId,
    ) : PageContent

    data class Scratch(
        val text: String,
        val revision: Long = 0,
    ) : PageContent {
        init {
            require(revision >= 0) { "revision must be non-negative" }
        }
    }
}

data class QueryHistoryState(
    val entries: List<String> = emptyList(),
    val capacity: Int = 100,
) {
    init {
        require(capacity > 0) { "capacity must be positive" }
        require(entries.size <= capacity) { "entries exceed capacity" }
    }

    fun record(query: String): QueryHistoryState {
        val normalised = query.trim()
        if (normalised.isEmpty() || entries.lastOrNull() == normalised) {
            return this
        }
        return copy(entries = (entries + normalised).takeLast(capacity))
    }
}

data class QueryState(
    val text: String = "",
    val history: QueryHistoryState = QueryHistoryState(),
)

data class TextStreamState(
    val text: String = "",
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    val hasUnreadChanges: Boolean get() = revision > seenRevision

    fun append(value: String): TextStreamState =
        if (value.isEmpty()) this else copy(text = text + value, revision = revision + 1)

    fun markRead(): TextStreamState = copy(seenRevision = revision)
}

data class WarningStreamState(
    val values: List<WarningPresentation> = emptyList(),
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    val hasUnreadChanges: Boolean get() = revision > seenRevision

    fun append(value: WarningPresentation): WarningStreamState = copy(values = values + value, revision = revision + 1)

    fun markRead(): WarningStreamState = copy(seenRevision = revision)
}

data class ConsoleState(
    val stdin: String = "",
    val stdout: TextStreamState = TextStreamState(),
    val stderr: TextStreamState = TextStreamState(),
    val warnings: WarningStreamState = WarningStreamState(),
)

data class DiagnosticState(
    val values: List<Diagnostic> = emptyList(),
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    val hasUnreadChanges: Boolean get() = revision > seenRevision

    fun replace(diagnostics: List<Diagnostic>): DiagnosticState = copy(values = diagnostics, revision = revision + 1)

    fun markRead(): DiagnosticState = copy(seenRevision = revision)
}

data class SolverSessionState(
    val lifecycle: SolverSessionLifecycle = SolverSessionLifecycle.ABSENT,
    val sessionId: SolverSessionId? = null,
    val profileId: SolverProfileId? = null,
    val loadedDocumentRevision: Long? = null,
    val capabilities: SolverCapabilities = SolverCapabilities.EMPTY,
    val inspection: SolverInspectionSnapshot = SolverInspectionSnapshot(),
    val error: String? = null,
) {
    val isFresh: Boolean get() = lifecycle == SolverSessionLifecycle.FRESH
}

enum class SolverSessionLifecycle {
    ABSENT,
    BUILDING,
    FRESH,
    STALE,
    FAILED,
}

enum class ResolutionStatus {
    IDLE,
    RUNNING,
    AWAITING_CONTINUATION,
    COMPLETED,
    FAILED,
    CANCELLED,
}

data class ResolutionState(
    val status: ResolutionStatus = ResolutionStatus.IDLE,
    val id: ResolutionSessionId? = null,
    val query: String? = null,
    val solutions: List<SolutionPresentation> = emptyList(),
    val error: String? = null,
    val revision: Long = 0,
    val seenRevision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
        require(seenRevision in 0..revision) { "seenRevision must be between zero and revision" }
    }

    val canSolve: Boolean
        get() =
            status in
                setOf(
                    ResolutionStatus.IDLE,
                    ResolutionStatus.COMPLETED,
                    ResolutionStatus.FAILED,
                    ResolutionStatus.CANCELLED,
                )

    val canContinue: Boolean
        get() = status == ResolutionStatus.AWAITING_CONTINUATION

    val canStop: Boolean
        get() = status == ResolutionStatus.RUNNING || status == ResolutionStatus.AWAITING_CONTINUATION

    val hasUnreadChanges: Boolean
        get() = revision > seenRevision

    fun markRead(): ResolutionState = copy(seenRevision = revision)
}

data class ResolutionHistoryEntry(
    val query: String,
    val solutions: List<SolutionPresentation>,
    val terminalStatus: ResolutionStatus,
    val error: String? = null,
)

data class PageHistoryState(
    val resolutions: List<ResolutionHistoryEntry> = emptyList(),
    val capacity: Int = 100,
) {
    init {
        require(capacity > 0) { "capacity must be positive" }
    }

    fun record(entry: ResolutionHistoryEntry): PageHistoryState =
        copy(resolutions = (resolutions + entry).takeLast(capacity))
}

sealed interface FeatureValue {
    data class Text(
        val value: String,
    ) : FeatureValue

    data class Number(
        val value: Double,
    ) : FeatureValue

    data class BooleanValue(
        val value: Boolean,
    ) : FeatureValue

    data class ListValue(
        val values: List<FeatureValue>,
    ) : FeatureValue

    data class ObjectValue(
        val values: Map<String, FeatureValue>,
    ) : FeatureValue

    data object NullValue : FeatureValue
}

data class PageFeatureState(
    val values: Map<String, FeatureValue> = emptyMap(),
    val revision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
    }
}

data class PageConfiguration(
    val solverProfileOverride: SolverProfileId? = null,
    val timeoutOverride: Duration? = null,
    val optionOverrides: Map<String, String> = emptyMap(),
)

data class WorkspaceConfiguration(
    val defaultSolverProfileId: SolverProfileId,
    val defaultTimeout: Duration = 5.seconds,
    val defaultOptions: Map<String, String> = emptyMap(),
    val schedulingPolicy: ResolutionSchedulingPolicy = ResolutionSchedulingPolicy.PER_PAGE_CONCURRENT,
)

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

data class PageState(
    val id: PageId,
    val title: String,
    val content: PageContent,
    val query: QueryState = QueryState(),
    val configuration: PageConfiguration = PageConfiguration(),
    val solverSession: SolverSessionState = SolverSessionState(),
    val resolution: ResolutionState = ResolutionState(),
    val history: PageHistoryState = PageHistoryState(),
    val console: ConsoleState = ConsoleState(),
    val diagnostics: DiagnosticState = DiagnosticState(),
    val semanticTokens: List<SemanticToken> = emptyList(),
    val features: Map<FeatureId, PageFeatureState> = emptyMap(),
)

data class WorkspaceState(
    val documents: Map<DocumentId, DocumentState> = emptyMap(),
    val pages: List<PageState> = emptyList(),
    val selectedPageId: PageId? = null,
    val configuration: WorkspaceConfiguration,
) {
    init {
        require(selectedPageId == null || pages.any { it.id == selectedPageId }) {
            "selectedPageId must reference an existing page"
        }
        for (page in pages) {
            val content = page.content
            if (content is PageContent.DocumentReference) {
                require(content.documentId in documents) {
                    "Page ${page.id} references missing document ${content.documentId}"
                }
            }
        }
    }

    fun page(id: PageId): PageState? = pages.firstOrNull { it.id == id }

    fun document(id: DocumentId): DocumentState? = documents[id]

    fun updatePage(
        id: PageId,
        transform: (PageState) -> PageState,
    ): WorkspaceState {
        var found = false
        val updated =
            pages.map {
                if (it.id == id) {
                    found = true
                    transform(it)
                } else {
                    it
                }
            }
        require(found) { "Unknown page: $id" }
        return copy(pages = updated)
    }

    fun updateDocument(
        id: DocumentId,
        transform: (DocumentState) -> DocumentState,
    ): WorkspaceState {
        val existing = documents[id] ?: error("Unknown document: $id")
        return copy(documents = documents + (id to transform(existing)))
    }
}

data class GuiState(
    val application: ApplicationState,
    val workspace: WorkspaceState,
)

enum class PanelId {
    SOLUTIONS,
    STDIN,
    STDOUT,
    STDERR,
    WARNINGS,
    DIAGNOSTICS,
    OPERATORS,
    FLAGS,
    LIBRARIES,
    STATIC_KB,
    DYNAMIC_KB,
}
