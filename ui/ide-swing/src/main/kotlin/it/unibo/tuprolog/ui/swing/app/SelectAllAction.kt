package it.unibo.tuprolog.ui.swing.app

import java.awt.event.ActionEvent
import javax.swing.text.DefaultEditorKit
import javax.swing.text.TextAction

/**
 * A [TextAction], like [DefaultEditorKit]'s own Cut/Copy/Paste actions, so it resolves its target through
 * [TextAction.getFocusedComponent] -- the JTextComponent-tracked "last focused" component -- rather than
 * [java.awt.KeyboardFocusManager], which a menu click can transiently null out before actionPerformed runs.
 */
internal class SelectAllAction : TextAction("select-all") {
    /** Selects all text in whichever [javax.swing.text.JTextComponent] last had focus. */
    override fun actionPerformed(event: ActionEvent?) {
        getFocusedComponent()?.selectAll()
    }
}
