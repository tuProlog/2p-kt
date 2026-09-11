package it.unibo.tuprolog.ui.gui.presentation

data class TextPosition(
    val offset: Int,
    val line: Int,
    val column: Int,
) {
    init {
        require(offset >= 0) { "offset must be non-negative" }
        require(line >= 0) { "line must be non-negative" }
        require(column >= 0) { "column must be non-negative" }
    }
}
