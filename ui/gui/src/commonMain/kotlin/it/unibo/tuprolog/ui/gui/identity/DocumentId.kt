package it.unibo.tuprolog.ui.gui.identity

/** Stable application-level identity. It is intentionally unrelated to a filesystem path. */
data class DocumentId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "DocumentId cannot be blank" }
    }

    override fun toString(): String = value
}
