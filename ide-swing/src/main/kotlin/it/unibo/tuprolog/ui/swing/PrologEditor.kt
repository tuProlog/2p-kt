package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.Font

/** Editable, parser-backed Prolog source area. */
internal class PrologEditor : RSyntaxTextArea() {
    private var operators: List<OperatorPresentation> = emptyList()
    private val analysisCache = PrologAnalysisCache(::sourceText, ::currentOperators)
    private val completionProvider = PrologCompletionProvider()
    private val syntaxParser = PrologSyntaxParser(analysisCache::analysis)

    val diagnostics: List<Diagnostic>
        get() = analysisCache.analysis().diagnostics

    init {
        document =
            RSyntaxDocument(PrologTokenMakerFactory(PrologTokenMaker(analysisCache::analysis)), PROLOG_SYNTAX_STYLE)
        font = Font(Font.MONOSPACED, Font.PLAIN, 14)
        isCodeFoldingEnabled = false
        isBracketMatchingEnabled = true
        setMarkOccurrences(true)
        setParserDelay(300)
        configurePrologSyntaxScheme()
        installZoomControls()
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

    internal fun categoryAt(offset: Int): String? = modelToToken(offset)?.type?.toPrologCategory()?.name

    internal fun completionCandidates(): List<PrologCompletion> = completionSuggestions(analysisCache.analysis())

    private fun sourceText(): String = text

    private fun currentOperators(): List<OperatorPresentation> = operators
}
