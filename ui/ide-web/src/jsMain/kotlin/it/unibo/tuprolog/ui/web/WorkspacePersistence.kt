package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

private const val STORAGE_KEY = "tuprolog-workspace"

/**
 * Persists open documents (each with its own query, query history, and resolution/Solutions-tree history) and
 * the shared editor's zoom level to the browser's `localStorage`, so ide-web can restore as much of its last
 * session as possible on reload - the same idea as ide-swing's `WorkspacePersistence`, just backed by
 * `localStorage` instead of a file, and without a per-page zoom level or window bounds (ide-web has one shared
 * editor and no window to speak of).
 */
object WebWorkspacePersistence {
    private val json = Json { ignoreUnknownKeys = true }

    fun load(): PersistedWorkspace? =
        runCatching {
            localStorage.getItem(STORAGE_KEY)?.let { json.decodeFromString(PersistedWorkspace.serializer(), it) }
        }.getOrNull()

    fun save(workspace: PersistedWorkspace) {
        val encoded = json.encodeToString(PersistedWorkspace.serializer(), workspace)
        runCatching { localStorage.setItem(STORAGE_KEY, encoded) }
    }

    /** Removes the persisted workspace, if any. Returns whether one was actually there to remove. */
    fun delete(): Boolean {
        val had = localStorage.getItem(STORAGE_KEY) != null
        localStorage.removeItem(STORAGE_KEY)
        return had
    }
}

/** Captures the pages currently open in [state] as a [PersistedWorkspace], ready to save. */
fun capturePersistedWorkspace(
    state: GuiState,
    fontSize: Int,
): PersistedWorkspace {
    val workspace = state.workspace
    val documents =
        workspace.pages.map { page ->
            val resolutions = page.history.resolutions.map { it.toPersisted() }
            val queryHistory = page.query.history.entries
            val query = page.query.text
            when (val content = page.content) {
                is PageContent.DocumentReference -> {
                    val document = workspace.document(content.documentId)
                    PersistedDocument(
                        displayName = document?.displayName ?: page.title,
                        text = document?.text.orEmpty(),
                        originProviderId = document?.origin?.providerId,
                        originReference = document?.origin?.opaqueReference,
                        dirty = document?.isDirty ?: false,
                        query = query,
                        queryHistory = queryHistory,
                        resolutions = resolutions,
                    )
                }
                is PageContent.Scratch ->
                    PersistedDocument(
                        displayName = page.title,
                        text = content.text,
                        isScratch = true,
                        query = query,
                        queryHistory = queryHistory,
                        resolutions = resolutions,
                    )
            }
        }
    return PersistedWorkspace(
        fontSize = fontSize,
        selectedIndex = workspace.pages.indexOfFirst { it.id == workspace.selectedPageId },
        documents = documents,
    )
}

/** Replays a previously-captured [PersistedWorkspace] as workspace/page actions against [controller]. */
suspend fun restoreWorkspace(
    persisted: PersistedWorkspace,
    controller: GuiController,
) {
    var selectedPageId: PageId? = null
    persisted.documents.forEachIndexed { index, document ->
        when {
            document.isScratch ->
                controller.dispatch(WorkspaceAction.NewScratchPage(document.displayName, document.text))
            document.originProviderId != null && document.originReference != null ->
                controller.dispatch(
                    WorkspaceAction.OpenDocumentLoaded(
                        origin =
                            DocumentOrigin(document.originProviderId, document.originReference, document.displayName),
                        text = document.text,
                        pending = document.dirty,
                    ),
                )
            else ->
                controller.dispatch(WorkspaceAction.NewDocumentPage(document.displayName, document.text))
        }
        val newPageId = controller.state.value.workspace.selectedPageId
        if (newPageId != null) {
            if (document.query.isNotEmpty()) {
                controller.dispatch(PageAction.ChangeQuery(newPageId, document.query))
            }
            if (document.queryHistory.isNotEmpty() || document.resolutions.isNotEmpty()) {
                controller.dispatch(
                    PageAction.RestoreHistory(
                        pageId = newPageId,
                        queryHistory = document.queryHistory,
                        resolutions = document.resolutions.map { it.toResolutionHistoryEntry() },
                    ),
                )
            }
        }
        if (index == persisted.selectedIndex) {
            selectedPageId = newPageId
        }
    }
    selectedPageId?.let { controller.dispatch(WorkspaceAction.SelectPage(it)) }
}
