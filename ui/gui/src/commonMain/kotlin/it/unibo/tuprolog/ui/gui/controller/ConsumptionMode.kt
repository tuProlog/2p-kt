package it.unibo.tuprolog.ui.gui.controller

enum class ConsumptionMode(
    val limit: Int?,
) {
    ONE(1),
    TEN(10),
    HUNDRED(100),
    ALL(null),
}
