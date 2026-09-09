package it.unibo.tuprolog.ui.swing

import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.DefaultCompletionProvider

internal class PrologCompletionProvider : DefaultCompletionProvider() {
    fun replace(completions: List<PrologCompletion>) {
        clear()
        completions.forEach { addCompletion(BasicCompletion(this, it.replacement, it.description)) }
    }
}
