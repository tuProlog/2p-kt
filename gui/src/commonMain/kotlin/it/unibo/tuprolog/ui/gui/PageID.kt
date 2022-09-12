package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.utils.io.File

sealed interface PageID {
    val name: String

    fun rename(name: String): PageID

    companion object {
        const val UNTITLED = "untitled"

        fun untitled(existing: Iterable<PageID> = emptyList()): PageID =
            existing.asSequence().distinct()
                .filter { it.name.startsWith(UNTITLED) }
                .map { it.name.removePrefix(UNTITLED) }
                .map { it.toIntOrNull() }
                .filterNotNull()
                .maxOrNull()
                ?.let { PageName(UNTITLED + (it + 1)) }
                ?: PageName(UNTITLED)

        fun name(name: String): PageName = PageName(name)

        fun file(path: String): FileName = file(File.of(path))

        fun file(file: File): FileName = FileName(file)
    }
}
