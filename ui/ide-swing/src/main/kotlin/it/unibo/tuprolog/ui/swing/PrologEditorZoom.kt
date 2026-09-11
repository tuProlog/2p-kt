package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.EditorZoom
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.event.InputEvent
import javax.swing.AbstractAction
import javax.swing.KeyStroke

internal fun RSyntaxTextArea.installZoomControls(onZoomed: (Int) -> Unit = {}) {
    fun zoom(delta: Int) {
        val newSize = EditorZoom.clamp(font.size + delta)
        font = font.deriveFont(newSize.toFloat())
        onZoomed(newSize)
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
