package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SourceSuggestion
import it.unibo.tuprolog.ui.gui.presentation.sourceIdentifierSuggestions
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.Font

/** Editable, parser-backed Prolog source area. */
internal class PrologEditor(
    initialFontSize: Int = 14,
) : RSyntaxTextArea() {
    private var operators: List<OperatorPresentation> = emptyList()
    private val analysisCache = PrologAnalysisCache(::sourceText, ::currentOperators)
    private val completionProvider = PrologCompletionProvider()
    private val syntaxParser = PrologSyntaxParser(analysisCache::analysis)

    /** Invoked whenever the user zooms this editor in or out, with the resulting font size. */
    var onZoomChanged: ((Int) -> Unit)? = null

    val diagnostics: List<Diagnostic>
        get() = analysisCache.analysis().diagnostics

    init {
        document =
            RSyntaxDocument(PrologTokenMakerFactory(PrologTokenMaker(analysisCache::analysis)), PROLOG_SYNTAX_STYLE)
        font = Font(Font.MONOSPACED, Font.PLAIN, initialFontSize)
        isCodeFoldingEnabled = false
        isBracketMatchingEnabled = true
        setMarkOccurrences(true)
        setParserDelay(300)
        configurePrologSyntaxScheme()
        installZoomControls { size -> onZoomChanged?.invoke(size) }
        addParser(syntaxParser)
        AutoCompletion(completionProvider).apply {
            isAutoActivationEnabled = true
            autoActivationDelay = 250
            isParameterAssistanceEnabled = true
            install(this@PrologEditor)
        }
    }

    fun highlight(operators: List<OperatorPresentation>) {
        if (this.operators != operators) {
            this.operators = operators
            analysisCache.invalidate()
            (document as RSyntaxDocument).setSyntaxStyle(PrologTokenMaker(analysisCache::analysis))
        }
        completionProvider.replace(completionCandidates())
        forceReparsing(syntaxParser)
    }

    internal fun categoryAt(offset: Int): String? = modelToToken(offset)?.type?.toSemanticCategory()?.name

    internal fun completionCandidates(): List<SourceSuggestion> = sourceIdentifierSuggestions(analysisCache.analysis())

    private fun sourceText(): String = text

    private fun currentOperators(): List<OperatorPresentation> = operators
}
