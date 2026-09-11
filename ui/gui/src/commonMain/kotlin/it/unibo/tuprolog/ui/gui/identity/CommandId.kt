package it.unibo.tuprolog.ui.gui.identity

data class CommandId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "CommandId cannot be blank" }
    }

    override fun toString(): String = value
}
