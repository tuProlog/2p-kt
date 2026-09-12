package it.unibo.tuprolog.ui.swing

import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Icon

internal class DotIcon(
    private val color: Color,
) : Icon {
    override fun getIconWidth() = 10

    override fun getIconHeight() = 10

    override fun paintIcon(
        component: Component?,
        graphics: Graphics,
        x: Int,
        y: Int,
    ) {
        val g2 = graphics.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = color
            g2.fillOval(x, y + 2, iconWidth, iconHeight)
        } finally {
            g2.dispose()
        }
    }
}
