package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.utils.io.File

data class FileName(val file: File) : PageID {
    override val name: String
        get() = file.name

    override fun rename(name: String): FileName = FileName(file.rename(name))
}
