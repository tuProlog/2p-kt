package it.unibo.tuprolog.ui.swing.plp

import java.awt.Font
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.JTextArea

private const val DOT_FONT_SIZE = 13

object DotTextSwingBddGraphRenderer : SwingBddGraphRenderer {
    override fun createGraphComponent(
        dot: String,
        title: String?,
    ): JComponent =
        JScrollPane(
            JTextArea(dot).apply {
                name = "bddDotTextArea"
                isEditable = false
                font = Font(Font.MONOSPACED, Font.PLAIN, DOT_FONT_SIZE)
                lineWrap = false
                toolTipText = title
            },
        )
}
