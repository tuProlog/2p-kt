package it.unibo.tuprolog.ui.gui.controller

/** How many solutions a "Solve"/"Next" action should request at once (backs the Solve/Solve 10/Solve 100/Solve
 * all buttons); [limit] is `null` for "keep going until exhausted". */
enum class ConsumptionMode(
    val limit: Int?,
) {
    ONE(1),
    TEN(10),
    HUNDRED(100),
    ALL(null),
}
