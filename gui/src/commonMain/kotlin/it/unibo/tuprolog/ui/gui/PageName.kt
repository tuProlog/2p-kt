package it.unibo.tuprolog.ui.gui

class PageName(override val name: String) : PageID {
    override fun rename(name: String): PageName = PageName(name)
}
