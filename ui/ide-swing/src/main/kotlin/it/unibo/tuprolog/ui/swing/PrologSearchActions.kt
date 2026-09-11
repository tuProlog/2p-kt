package it.unibo.tuprolog.ui.swing

import org.fife.rsta.ui.GoToDialog
import org.fife.rsta.ui.search.FindDialog
import org.fife.rsta.ui.search.ReplaceDialog
import org.fife.rsta.ui.search.SearchEvent
import org.fife.rsta.ui.search.SearchListener
import org.fife.ui.rtextarea.SearchContext
import org.fife.ui.rtextarea.SearchEngine
import java.awt.Frame
import java.awt.Toolkit

/** RSTAUI search dialogs bound to the editor selected in the Swing workspace. */
internal class PrologSearchActions(
    private val parent: Frame,
    private val selectedEditor: () -> PrologEditor?,
) : SearchListener {
    private val searchContext = SearchContext()
    private val findDialog = FindDialog(parent, this).apply { searchContext = this@PrologSearchActions.searchContext }
    private val replaceDialog =
        ReplaceDialog(parent, this).apply {
            searchContext =
                this@PrologSearchActions.searchContext
        }

    fun showFind() {
        findDialog.isVisible = true
    }

    fun showReplace() {
        replaceDialog.isVisible = true
    }

    fun showGoToLine() {
        val editor = selectedEditor() ?: return
        GoToDialog(parent).apply {
            setMaxLineNumberAllowed(editor.lineCount)
            isVisible = true
            val line = lineNumber - 1
            if (line in 0 until editor.lineCount) {
                editor.caretPosition = editor.getLineStartOffset(line)
                editor.requestFocusInWindow()
            }
        }
    }

    override fun getSelectedText(): String? = selectedEditor()?.selectedText

    override fun searchEvent(event: SearchEvent) {
        val editor = selectedEditor() ?: return
        val result =
            when (event.type) {
                SearchEvent.Type.FIND -> SearchEngine.find(editor, event.searchContext)
                SearchEvent.Type.REPLACE -> SearchEngine.replace(editor, event.searchContext)
                SearchEvent.Type.REPLACE_ALL -> SearchEngine.replaceAll(editor, event.searchContext)
                SearchEvent.Type.MARK_ALL -> SearchEngine.markAll(editor, event.searchContext)
            }
        if (!result.wasFound()) Toolkit.getDefaultToolkit().beep()
    }
}
