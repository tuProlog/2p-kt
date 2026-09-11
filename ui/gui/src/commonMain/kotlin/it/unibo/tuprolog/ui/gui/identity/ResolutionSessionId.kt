package it.unibo.tuprolog.ui.gui.identity

data class ResolutionSessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "ResolutionSessionId cannot be blank" }
    }

    override fun toString(): String = value
}
