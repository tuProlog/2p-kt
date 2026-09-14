package it.unibo.tuprolog.ui.gui.identity

/** Identifies one registered `GuiExtension`, unique across every extension in the same application. */
data class ExtensionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ExtensionId cannot be blank" }
    }

    override fun toString(): String = value
}
