package it.unibo.tuprolog.ui.gui.identity

data class FeatureId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "FeatureId cannot be blank" }
    }

    override fun toString(): String = value
}
