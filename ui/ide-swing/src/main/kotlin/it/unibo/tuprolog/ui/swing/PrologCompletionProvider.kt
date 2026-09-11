package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.SourceSuggestion
import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.DefaultCompletionProvider

internal class PrologCompletionProvider : DefaultCompletionProvider() {
    fun replace(completions: List<SourceSuggestion>) {
        clear()
        completions.forEach { addCompletion(BasicCompletion(this, it.text, it.category)) }
    }
}
