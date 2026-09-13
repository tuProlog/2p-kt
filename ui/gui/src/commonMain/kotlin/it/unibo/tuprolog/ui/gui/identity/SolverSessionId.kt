package it.unibo.tuprolog.ui.gui.identity

/** Identifies one live `SolverSession` (the solver instance backing a single page), unique across the
 * application while that session is alive. */
data class SolverSessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SolverSessionId cannot be blank" }
    }

    override fun toString(): String = value
}
