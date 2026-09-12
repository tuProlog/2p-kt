package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.persistence.PersistedWorkspace
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

private const val STORAGE_KEY = "tuprolog-workspace"

/**
 * Persists open documents (each with its own query, query history, and resolution/Solutions-tree history) and
 * the shared editor's zoom level to the browser's `localStorage`, so ide-web can restore as much of its last
 * session as possible on reload. The [PersistedWorkspace] shape and the logic to capture/restore one (see
 * `it.unibo.tuprolog.ui.web.capturePersistedWorkspace`/`restoreWorkspace`, re-exported from `ui/gui`'s
 * `it.unibo.tuprolog.ui.gui.persistence` package) are shared with other frontends (e.g. ide-swing); only this
 * `localStorage`-backed storage is ide-web-specific.
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
