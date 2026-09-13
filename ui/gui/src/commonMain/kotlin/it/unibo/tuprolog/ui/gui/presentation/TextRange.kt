package it.unibo.tuprolog.ui.gui.presentation

/** A span of source text from [start] up to (but not including) [endExclusive]. */
data class TextRange(
    val start: TextPosition,
    val endExclusive: TextPosition,
) {
    init {
        require(start.offset <= endExclusive.offset) { "range start must not follow its end" }
    }
}
