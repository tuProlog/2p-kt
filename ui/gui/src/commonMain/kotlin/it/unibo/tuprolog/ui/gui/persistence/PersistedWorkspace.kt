package it.unibo.tuprolog.ui.gui.persistence

import kotlinx.serialization.Serializable

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
    /** This page's own editor zoom level, where the frontend supports per-page zoom; `null` elsewhere, or to
     * restore it at the workspace-wide default instead. */
    val fontSize: Int? = null,
    /** Past (concluded) resolutions - restores the Solutions tree's content without re-running anything. */
    val resolutions: List<PersistedResolution> = emptyList(),
)

@Serializable
data class PersistedWorkspace(
    val fontSize: Int = 14,
    /** Window bounds, where the frontend has a window to speak of; `null` otherwise. */
    val windowWidth: Int? = null,
    val windowHeight: Int? = null,
    val windowX: Int? = null,
    val windowY: Int? = null,
    val selectedIndex: Int = -1,
    val documents: List<PersistedDocument> = emptyList(),
    /** Display name of the look-and-feel active when last saved, e.g. "Nimbus", where the frontend has one;
     * `null` keeps the frontend's default otherwise. */
    val lookAndFeel: String? = null,
)
