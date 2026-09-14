package it.unibo.tuprolog.ui.gui.identity

/** Identifies one dispatched `GuiEffect`, so the toolkit-specific handler that eventually performs it can be
 * correlated back to the effect that requested it (e.g. for logging or de-duplication). */
data class EffectId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "EffectId cannot be blank" }
    }

    override fun toString(): String = value
}
