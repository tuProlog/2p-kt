package it.unibo.tuprolog.ui.gui.identity

/** Identifies one `FeatureDescriptor` (a toolkit-rendered tab/panel an extension contributes, e.g. PLP's BDD
 * inspector), unique across the application. */
data class FeatureId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "FeatureId cannot be blank" }
    }

    override fun toString(): String = value
}
