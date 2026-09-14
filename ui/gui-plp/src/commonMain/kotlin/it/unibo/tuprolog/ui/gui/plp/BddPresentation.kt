package it.unibo.tuprolog.ui.gui.plp

/** A probabilistic solution's binary decision diagram, ready for a toolkit-specific renderer to draw. */
data class BddPresentation(
    val dot: String,
    val title: String? = null,
) {
    init {
        require(dot.isNotBlank()) { "BDD DOT representation cannot be blank" }
    }
}
