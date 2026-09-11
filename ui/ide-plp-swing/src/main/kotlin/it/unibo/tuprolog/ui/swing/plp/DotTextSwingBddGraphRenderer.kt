package it.unibo.tuprolog.ui.swing.plp

import java.awt.Font
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.JTextArea

object DotTextSwingBddGraphRenderer : SwingBddGraphRenderer {
    override fun createGraphComponent(
        dot: String,
        title: String?,
    ): JComponent =
        JScrollPane(
            JTextArea(dot).apply {
                isEditable = false
                font = Font(Font.MONOSPACED, Font.PLAIN, 13)
                lineWrap = false
                toolTipText = title
            },
        )
}
