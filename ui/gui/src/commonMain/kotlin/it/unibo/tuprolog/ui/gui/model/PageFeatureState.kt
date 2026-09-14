package it.unibo.tuprolog.ui.gui.model

/** One extension feature's current values for a page (e.g. PLP's BDD/probability), plus a revision counter for
 * unread-changes tracking. */
data class PageFeatureState(
    val values: Map<String, FeatureValue> = emptyMap(),
    val revision: Long = 0,
) {
    init {
        require(revision >= 0) { "revision must be non-negative" }
    }
}
