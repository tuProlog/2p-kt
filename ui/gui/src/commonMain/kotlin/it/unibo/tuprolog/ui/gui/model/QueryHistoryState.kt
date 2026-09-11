package it.unibo.tuprolog.ui.gui.model

data class QueryHistoryState(
    val entries: List<String> = emptyList(),
    val capacity: Int = 100,
) {
    init {
        require(capacity > 0) { "capacity must be positive" }
        require(entries.size <= capacity) { "entries exceed capacity" }
    }

    fun record(query: String): QueryHistoryState {
        val normalised = query.trim()
        if (normalised.isEmpty() || entries.lastOrNull() == normalised) {
            return this
        }
        return copy(entries = (entries + normalised).takeLast(capacity))
    }
}
