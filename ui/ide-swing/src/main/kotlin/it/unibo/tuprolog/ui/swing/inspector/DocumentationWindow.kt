package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.markdownToHtml
import java.awt.Component
import java.awt.Dimension
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JEditorPane
import javax.swing.JScrollPane
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

private const val DOCUMENTATION_WIDTH = 640
private const val DOCUMENTATION_HEIGHT = 480

/**
 * Opens a window titled [title], next to [owner], showing [markdown] rendered; does nothing if [markdown] is blank.
 * The window closes on Esc.
 */
internal fun showDocumentationWindow(
    owner: Component,
    title: String,
    markdown: String?,
) {
    if (markdown.isNullOrBlank()) return
    val pane =
        JEditorPane("text/html", markdownToHtml(markdown)).apply {
            name = "documentationPane"
            isEditable = false
            putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
            font = owner.font
            caretPosition = 0
        }
    JDialog(SwingUtilities.getWindowAncestor(owner), title).apply {
        name = "documentationWindow"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        contentPane.add(JScrollPane(pane))
        rootPane.registerKeyboardAction(
            { dispose() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW,
        )
        size = Dimension(DOCUMENTATION_WIDTH, DOCUMENTATION_HEIGHT)
        setLocationRelativeTo(owner)
        isVisible = true
    }
}
