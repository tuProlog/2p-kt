package it.unibo.tuprolog.ui.gui.persistence

import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent

/**
 * Captures the pages currently open in [state] as a [PersistedWorkspace], ready to save - shared by every
 * frontend (ide-swing, ide-web, ...) regardless of how it actually stores the result (a file, `localStorage`,
 * ...). [pageFontSizes] is consulted for each page's own zoom level, where the frontend supports one, falling
 * back to the workspace-wide [fontSize] when a page never had its own; [windowWidth]/[windowHeight]/[windowX]/
 * [windowY]/[lookAndFeel] are left to `null` by frontends with no window or look-and-feel concept (e.g. ide-web).
 */
fun capturePersistedWorkspace(
    state: GuiState,
    fontSize: Int,
    pageFontSizes: Map<PageId, Int> = emptyMap(),
    windowWidth: Int? = null,
    windowHeight: Int? = null,
    windowX: Int? = null,
    windowY: Int? = null,
    lookAndFeel: String? = null,
): PersistedWorkspace {
    val workspace = state.workspace
    val documents =
        workspace.pages.map { page ->
            val resolutions = page.history.resolutions.map { it.toPersisted() }
            val queryHistory = page.query.history.entries
            val query = page.query.text
            val pageFontSize = pageFontSizes[page.id]
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
                        fontSize = pageFontSize,
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
                        fontSize = pageFontSize,
                        resolutions = resolutions,
                    )
            }
        }
    return PersistedWorkspace(
        fontSize = fontSize,
        windowWidth = windowWidth,
        windowHeight = windowHeight,
        windowX = windowX,
        windowY = windowY,
        selectedIndex = workspace.pages.indexOfFirst { it.id == workspace.selectedPageId },
        documents = documents,
        lookAndFeel = lookAndFeel,
    )
}

/**
 * Replays a previously-captured [PersistedWorkspace] as workspace/page actions against [controller] - every
 * document, each with its query, query history, and resolution/Solutions-tree history. Applying the workspace's
 * look-and-feel, window bounds, and each page's own editor zoom level (where the frontend has any of those) is
 * frontend-specific and left to the caller; this function only touches the toolkit-neutral [GuiController] state.
 */
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
