package it.unibo.tuprolog.ui.gui

interface File : PageID {
    val path: String

    fun readText(): String

    fun writeText(text: String)
}
