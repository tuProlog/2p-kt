package it.unibo.tuprolog.ui.swing

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.event.InputEvent
import javax.swing.AbstractAction
import javax.swing.KeyStroke

internal fun RSyntaxTextArea.installZoomControls() {
    fun zoom(delta: Int) {
        font = font.deriveFont((font.size + delta).coerceIn(8, 48).toFloat())
    }
    actionMap.put(
        "zoom-in",
        object : AbstractAction() {
            override fun actionPerformed(event: java.awt.event.ActionEvent?) = zoom(1)
        },
    )
    actionMap.put(
        "zoom-out",
        object : AbstractAction() {
            override fun actionPerformed(event: java.awt.event.ActionEvent?) = zoom(-1)
        },
    )
    listOf(InputEvent.CTRL_DOWN_MASK, InputEvent.META_DOWN_MASK).forEach { modifier ->
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, modifier), "zoom-in")
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, modifier), "zoom-in")
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, modifier), "zoom-out")
    }
    addMouseWheelListener { event ->
        if (event.isControlDown || event.isMetaDown) {
            zoom(if (event.preciseWheelRotation < 0) 1 else -1)
            event.consume()
        }
    }
}
