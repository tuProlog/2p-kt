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
