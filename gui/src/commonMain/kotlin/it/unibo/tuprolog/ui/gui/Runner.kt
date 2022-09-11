package it.unibo.tuprolog.ui.gui

interface Runner {
    fun ui(action: () -> Unit)
    fun background(action: () -> Unit)
    fun io(action: () -> Unit)
}
