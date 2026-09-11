package it.unibo.tuprolog.ui.gui.model

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
