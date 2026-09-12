package it.unibo.tuprolog.ui.swing

import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

/** A same-sized, fully transparent stand-in icon - see [SolutionCellRenderer.currentIcon]. */
internal class BlankIcon(
    private val width: Int,
    private val height: Int,
) : Icon {
    override fun getIconWidth() = width

    override fun getIconHeight() = height

    override fun paintIcon(
        component: Component?,
        graphics: Graphics,
        x: Int,
        y: Int,
    ) = Unit
}
