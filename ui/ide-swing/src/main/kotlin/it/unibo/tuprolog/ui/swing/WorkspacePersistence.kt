package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.persistence.PersistedWorkspace
import it.unibo.tuprolog.ui.gui.persistence.capturePersistedWorkspace
import kotlinx.serialization.json.Json
import java.awt.Rectangle
import java.io.File
import javax.swing.UIManager

/**
 * Persists open documents (each with its own query, query history, resolution/Solutions-tree history, and
 * editor zoom level), the active look-and-feel, and window bounds to a hidden per-app JSON file under the
 * user's home directory, so a Swing IDE variant can restore as much of its last session as possible on startup.
 * The [PersistedWorkspace] shape and the logic to capture/restore one are shared with other frontends (e.g.
 * ide-web) via `ui/gui`'s `it.unibo.tuprolog.ui.gui.persistence` package; only this file-based storage is
 * ide-swing-specific.
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

/** ide-swing-specific wrapper around the shared `capturePersistedWorkspace`, adding window bounds and the
 * active look-and-feel - the two pieces of workspace state only a windowed Swing frontend has. */
fun capturePersistedWorkspace(
    state: GuiState,
    fontSize: Int,
    windowBounds: Rectangle?,
    pageFontSizes: Map<PageId, Int> = emptyMap(),
): PersistedWorkspace =
    capturePersistedWorkspace(
        state = state,
        fontSize = fontSize,
        pageFontSizes = pageFontSizes,
        windowWidth = windowBounds?.width,
        windowHeight = windowBounds?.height,
        windowX = windowBounds?.x,
        windowY = windowBounds?.y,
        lookAndFeel = UIManager.getLookAndFeel()?.name,
    )
