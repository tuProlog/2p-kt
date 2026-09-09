package it.unibo.tuprolog.ui.swing

internal data class ColouredToken(
    val start: Int,
    val length: Int,
    val category: PrologCategory,
)
