package it.unibo.tuprolog.ui.gui.model

data class PageFeatureState(
    val values: Map<String, FeatureValue> = emptyMap(),
    val revision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
    }
}
