package it.unibo.tuprolog.ui.gui.identity

/** Identifies one `CommandDescriptor` an extension contributed, unique within its owning extension. */
data class CommandId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "CommandId cannot be blank" }
    }

    override fun toString(): String = value
}
