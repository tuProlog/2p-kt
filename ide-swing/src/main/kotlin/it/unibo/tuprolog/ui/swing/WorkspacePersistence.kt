package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.DocumentOrigin
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class PersistedDocument(
    val displayName: String,
    val text: String,
    val originProviderId: String? = null,
    val originReference: String? = null,
    val dirty: Boolean = false,
    /** Whether this was a page-local scratch buffer rather than a (possibly still-untitled) document. */
    val isScratch: Boolean = false,
)

@Serializable
data class PersistedWorkspace(
    val fontSize: Int = 14,
    val windowWidth: Int? = null,
    val windowHeight: Int? = null,
    val windowX: Int? = null,
    val windowY: Int? = null,
    val selectedIndex: Int = -1,
    val documents: List<PersistedDocument> = emptyList(),
)

/**
 * Persists open documents, editor font size, and window bounds to a hidden per-app JSON file under the user's
 * home directory, so a Swing IDE variant can restore its last session automatically on startup.
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
}

/** Captures the pages currently open in [state] as a [PersistedWorkspace], ready to save. */
fun capturePersistedWorkspace(
    state: GuiState,
    fontSize: Int,
    windowBounds: java.awt.Rectangle?,
): PersistedWorkspace {
    val workspace = state.workspace
    val documents =
        workspace.pages.map { page ->
            when (val content = page.content) {
                is PageContent.DocumentReference -> {
                    val document = workspace.document(content.documentId)
                    PersistedDocument(
                        displayName = document?.displayName ?: page.title,
                        text = document?.text.orEmpty(),
                        originProviderId = document?.origin?.providerId,
                        originReference = document?.origin?.opaqueReference,
                        dirty = document?.isDirty ?: false,
                    )
                }
                is PageContent.Scratch ->
                    PersistedDocument(displayName = page.title, text = content.text, isScratch = true)
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
    )
}

/** Replays a previously-captured [PersistedWorkspace] as workspace actions against [controller]. */
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
        if (index == persisted.selectedIndex) {
            selectedPageId = controller.state.value.workspace.selectedPageId
        }
    }
    selectedPageId?.let { controller.dispatch(WorkspaceAction.SelectPage(it)) }
}
