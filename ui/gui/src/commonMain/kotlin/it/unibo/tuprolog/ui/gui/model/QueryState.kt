package it.unibo.tuprolog.ui.gui.model

data class QueryState(
    val text: String = "",
    val history: QueryHistoryState = QueryHistoryState(),
)
