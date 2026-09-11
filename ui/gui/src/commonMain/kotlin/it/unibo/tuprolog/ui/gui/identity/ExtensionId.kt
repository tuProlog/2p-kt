package it.unibo.tuprolog.ui.gui.identity

data class ExtensionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ExtensionId cannot be blank" }
    }

    override fun toString(): String = value
}
