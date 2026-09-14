package it.unibo.tuprolog.ui.gui.model

/** A page's current query text plus its submission history. */
data class QueryState(
    val text: String = "",
    val history: QueryHistoryState = QueryHistoryState(),
)
