package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken
import it.unibo.tuprolog.ui.web.ace.Ace
import it.unibo.tuprolog.ui.web.ace.AceEditor
import it.unibo.tuprolog.ui.web.ace.AceToken
import it.unibo.tuprolog.ui.web.ace.aceAnnotation
import it.unibo.tuprolog.ui.web.ace.aceCustomMode
import it.unibo.tuprolog.ui.web.ace.aceToken
import org.w3c.dom.HTMLElement

/** Thin, Kotlin-friendly adapter around the [Ace] editor, hiding its JS-shaped API from the rest of the view. */
internal class AceEditorView(
    container: HTMLElement,
) {
    private val editor: AceEditor = Ace.edit(container)

    // Classifies every line by reusing :gui's own parser/lexer-backed analysis (see setSemanticTokens) instead
    // of Ace's regex-rule-based highlighting; the mode object itself never changes, only the tokens it reads.
    private var semanticTokens: List<SemanticToken> = emptyList()
    private val mode: dynamic = aceCustomMode(::lineTokens)

    init {
        editor.setTheme("ace/theme/github")
        editor.session.setMode(mode)
    }

    var value: String
        get() = editor.getValue()
        set(text) {
            if (text != editor.getValue()) editor.setValue(text, 1)
        }

    var readOnly: Boolean
        get() = false
        set(value) = editor.setReadOnly(value)

    val isFocused: Boolean get() = editor.isFocused()

    fun onChange(callback: () -> Unit) {
        editor.session.on("change") { callback() }
    }

    fun resize() = editor.resize(true)

    fun setDiagnostics(diagnostics: List<Diagnostic>) {
        val annotations =
            diagnostics.mapNotNull { diagnostic ->
                val start = diagnostic.range?.start ?: return@mapNotNull null
                aceAnnotation(
                    row = start.line,
                    column = start.column,
                    text = diagnostic.message,
                    type = diagnostic.severity.toAceType(),
                )
            }
        if (annotations.isEmpty()) {
            editor.session.clearAnnotations()
        } else {
            editor.session.setAnnotations(
                annotations.toTypedArray(),
            )
        }
    }

    private fun DiagnosticSeverity.toAceType(): String =
        when (this) {
            DiagnosticSeverity.ERROR -> "error"
            DiagnosticSeverity.WARNING -> "warning"
            DiagnosticSeverity.INFO -> "info"
        }

    fun setSemanticTokens(tokens: List<SemanticToken>) {
        if (tokens == semanticTokens) return
        semanticTokens = tokens
        // Re-applying the (identical) mode object is how Ace's own API forces every visible line to be
        // re-tokenized; there is no lower-level "invalidate" call exposed for a session-level custom tokenizer.
        editor.session.setMode(mode)
    }

    private fun lineTokens(
        row: Int,
        line: String,
    ): Array<AceToken> =
        semanticTokensForLine(row, line, semanticTokens)
            .map { aceToken(it.category.toAceTokenType(), it.text) }
            .toTypedArray()

    private fun SemanticCategory?.toAceTokenType(): String =
        when (this) {
            null -> "text"
            SemanticCategory.COMMENT -> "comment"
            SemanticCategory.OPERATOR -> "keyword.operator"
            SemanticCategory.PARENTHESIS, SemanticCategory.BRACE, SemanticCategory.BRACKET -> "paren"
            SemanticCategory.FUNCTOR -> "support.function"
            SemanticCategory.ATOM -> "identifier"
            SemanticCategory.VARIABLE -> "variable"
            SemanticCategory.NUMBER -> "constant.numeric"
            SemanticCategory.STRING -> "string"
            SemanticCategory.FULL_STOP -> "punctuation"
            SemanticCategory.DIRECTIVE -> "keyword"
            SemanticCategory.ERROR -> "invalid"
        }
}
