package it.unibo.tuprolog.ui.gui.identity

data class SolverProfileId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SolverProfileId cannot be blank" }
    }

    override fun toString(): String = value
}
