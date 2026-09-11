package it.unibo.tuprolog.ui.gui.identity

data class EffectId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "EffectId cannot be blank" }
    }

    override fun toString(): String = value
}
