package it.unibo.tuprolog.ui.swing.editor

import it.unibo.tuprolog.ui.gui.presentation.SourceSuggestion
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.DefaultCompletionProvider

/** Feeds RSTA's autocomplete popup from this codebase's own [SourceSuggestion] analysis, refreshed on every edit. */
internal class PrologCompletionProvider : DefaultCompletionProvider() {
    /** Discards every previously offered completion and replaces it with one per [completions]. */
    fun replace(completions: List<SourceSuggestion>) {
        clear()
        completions.forEach { addCompletion(BasicCompletion(this, it.text, it.category)) }
    }
}
