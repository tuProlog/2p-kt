package it.unibo.tuprolog.ui.gui.model

data class PageHistoryState(
    val resolutions: List<ResolutionHistoryEntry> = emptyList(),
    val capacity: Int = 100,
) {
    init {
        require(capacity > 0) { "capacity must be positive" }
    }

    fun record(entry: ResolutionHistoryEntry): PageHistoryState =
        copy(resolutions = (resolutions + entry).takeLast(capacity))
}
