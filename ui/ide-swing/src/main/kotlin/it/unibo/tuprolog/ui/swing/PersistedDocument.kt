package it.unibo.tuprolog.ui.swing

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
)
