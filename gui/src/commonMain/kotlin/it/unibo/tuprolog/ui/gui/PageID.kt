package it.unibo.tuprolog.ui.gui

sealed interface PageID {
    val name: String

    fun rename(name: String) : PageID

    companion object {
        private const val UNTITLED = "untitled"

        fun untitled(existing: Iterable<PageID> = emptyList()): PageID =
            existing.asSequence().distinct()
                .filter { it.name.startsWith(UNTITLED) }
                .map { it.name.removePrefix(UNTITLED) }
                .map { it.toIntOrNull() }
                .filterNotNull()
                .maxOrNull()
                ?.let { PageName(UNTITLED + (it + 1)) }
                ?: PageName(UNTITLED)
    }
}
