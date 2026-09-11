package it.unibo.tuprolog.ui.gui.identity

data class SolverSessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SolverSessionId cannot be blank" }
    }

    override fun toString(): String = value
}
