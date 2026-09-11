package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.sourceIdentifierSuggestions
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.Font
import javax.swing.AbstractAction
import javax.swing.KeyStroke
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter

/** Single-line, syntax-coloured query editor. */
internal class PrologQueryField : RSyntaxTextArea() {
    var onSubmit: (() -> Unit)? = null

    private var operators: List<OperatorPresentation> = emptyList()
    private val analysisCache = PrologAnalysisCache(::sourceText, ::currentOperators)
    private val completionProvider = PrologCompletionProvider()

    init {
        document =
            RSyntaxDocument(PrologTokenMakerFactory(PrologTokenMaker(analysisCache::analysis)), PROLOG_SYNTAX_STYLE)
        font = Font(Font.MONOSPACED, Font.PLAIN, 14)
        rows = 1
        lineWrap = true
        isCodeFoldingEnabled = false
        isBracketMatchingEnabled = true
        setHighlightCurrentLine(false)
        configurePrologSyntaxScheme()
        installZoomControls()
        (document as AbstractDocument).documentFilter = SingleLineFilter()
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "submit-query")
        actionMap.put(
            "submit-query",
            object : AbstractAction() {
                override fun actionPerformed(event: java.awt.event.ActionEvent?) {
                    onSubmit?.invoke()
                }
            },
        )
        AutoCompletion(completionProvider).apply {
            isAutoActivationEnabled = true
            autoActivationDelay = 250
            install(this@PrologQueryField)
        }
    }

    fun highlight(operators: List<OperatorPresentation>) {
        if (this.operators != operators) {
            this.operators = operators
            analysisCache.invalidate()
            (document as RSyntaxDocument).setSyntaxStyle(PrologTokenMaker(analysisCache::analysis))
        }
        completionProvider.replace(sourceIdentifierSuggestions(analysisCache.analysis()))
    }

    private fun sourceText(): String = text

    private fun currentOperators(): List<OperatorPresentation> = operators

    private class SingleLineFilter : DocumentFilter() {
        override fun insertString(
            fb: FilterBypass,
            offset: Int,
            string: String,
            attr: AttributeSet?,
        ) = super.insertString(fb, offset, string.singleLine(), attr)

        override fun replace(
            fb: FilterBypass,
            offset: Int,
            length: Int,
            text: String?,
            attrs: AttributeSet?,
        ) = super.replace(fb, offset, length, text.orEmpty().singleLine(), attrs)

        private fun String.singleLine(): String = replace("\n", "").replace("\r", "")
    }
}
