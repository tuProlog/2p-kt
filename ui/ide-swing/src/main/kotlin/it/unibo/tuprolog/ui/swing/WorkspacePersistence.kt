package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import kotlinx.serialization.json.Json
import java.io.File
import javax.swing.UIManager

/**
 * Persists open documents (each with its own query, query history, resolution/Solutions-tree history, and
 * editor zoom level), the active look-and-feel, and window bounds to a hidden per-app JSON file under the
 * user's home directory, so a Swing IDE variant can restore as much of its last session as possible on startup.
 */
class WorkspacePersistence(
    appName: String,
    baseDir: File = File(System.getProperty("user.home"), ".2p-kt"),
) {
    private val file = File(baseDir, "$appName/workspace.json")
    private val json = Json { ignoreUnknownKeys = true }

    fun load(): PersistedWorkspace? =
        runCatching {
            file.takeIf(File::exists)?.readText()?.let { json.decodeFromString(PersistedWorkspace.serializer(), it) }
        }.getOrNull()

    fun save(workspace: PersistedWorkspace) {
        runCatching {
            file.parentFile?.mkdirs()
            file.writeText(json.encodeToString(PersistedWorkspace.serializer(), workspace))
        }
    }

    /** Removes the persisted file, if any. Returns whether a file was actually there to remove. */
    fun delete(): Boolean = runCatching { file.delete() }.getOrDefault(false)
}

/**
 * Captures the pages currently open in [state] as a [PersistedWorkspace], ready to save. [pageFontSizes] is
 * consulted for each page's own zoom level (see [SwingIdeFrame.pageFontSizes]), falling back to the
 * workspace-wide [fontSize] when a page never had its own.
 */
fun capturePersistedWorkspace(
    state: GuiState,
    fontSize: Int,
    windowBounds: java.awt.Rectangle?,
    pageFontSizes: Map<PageId, Int> = emptyMap(),
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
        windowWidth = windowBounds?.width,
        windowHeight = windowBounds?.height,
        windowX = windowBounds?.x,
        windowY = windowBounds?.y,
        selectedIndex = workspace.pages.indexOfFirst { it.id == workspace.selectedPageId },
        documents = documents,
        lookAndFeel = UIManager.getLookAndFeel()?.name,
    )
}

/**
 * Replays a previously-captured [PersistedWorkspace] as workspace/page actions against [controller] - every
 * document, each with its query, query history, and resolution/Solutions-tree history. Applying the
 * workspace's look-and-feel and each page's own editor zoom level is Swing-frame-specific and left to the
 * caller (see [SwingIdeApplication.show]); this function only touches the toolkit-neutral [GuiController] state.
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
