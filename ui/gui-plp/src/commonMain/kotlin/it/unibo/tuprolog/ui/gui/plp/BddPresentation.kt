package it.unibo.tuprolog.ui.gui.plp

data class BddPresentation(
    val dot: String,
    val title: String? = null,
) {
    init {
        require(dot.isNotBlank()) { "BDD DOT representation cannot be blank" }
    }
}
