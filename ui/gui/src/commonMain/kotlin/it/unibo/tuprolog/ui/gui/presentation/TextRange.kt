package it.unibo.tuprolog.ui.gui.presentation

data class TextRange(
    val start: TextPosition,
    val endExclusive: TextPosition,
) {
    init {
        require(start.offset <= endExclusive.offset) { "range start must not follow its end" }
    }
}
