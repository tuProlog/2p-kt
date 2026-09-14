package it.unibo.tuprolog.ui.gui.identity

/** Identifies one registered `SolverProfile` (a selectable solver configuration, e.g. "Prolog" or "Problog"),
 * unique across the application. */
data class SolverProfileId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SolverProfileId cannot be blank" }
    }

    override fun toString(): String = value
}
