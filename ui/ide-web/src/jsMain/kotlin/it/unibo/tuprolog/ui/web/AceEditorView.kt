package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.EditorZoom
import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken
import it.unibo.tuprolog.ui.web.ace.Ace
import it.unibo.tuprolog.ui.web.ace.AceEditor
import it.unibo.tuprolog.ui.web.ace.AceToken
import it.unibo.tuprolog.ui.web.ace.aceAnnotation
import it.unibo.tuprolog.ui.web.ace.aceCustomMode
import it.unibo.tuprolog.ui.web.ace.aceToken
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.WheelEvent

/** Thin, Kotlin-friendly adapter around the [Ace] editor, hiding its JS-shaped API from the rest of the view. */
internal class AceEditorView(
    container: HTMLElement,
) {
    private val editor: AceEditor = Ace.edit(container)

    // Classifies every line by reusing :gui's own parser/lexer-backed analysis (see setSemanticTokens) instead
    // of Ace's regex-rule-based highlighting; the mode object itself never changes, only the tokens it reads.
    private var semanticTokens: List<SemanticToken> = emptyList()
    private val mode: dynamic = aceCustomMode(::lineTokens)
    private var fontSize = EditorZoom.DEFAULT_FONT_SIZE

    init {
        val darkModeQuery = window.matchMedia("(prefers-color-scheme: dark)")
        applyTheme(darkModeQuery.matches)
        darkModeQuery.addEventListener("change", { _: Event -> applyTheme(darkModeQuery.matches) })
        editor.session.setMode(mode)
        container.addEventListener("keydown", { event: Event -> handleZoomKeydown(event as KeyboardEvent) })
        // Wheel listeners default to passive (preventDefault() is a no-op) unless told otherwise, since a
        // handler that never calls it would otherwise block the browser's scroll-performance optimizations.
        container.addEventListener(
            "wheel",
            { event: Event -> handleZoomWheel(event as WheelEvent) },
            js("({ passive: false })"),
        )
    }

    private fun applyTheme(dark: Boolean) {
        editor.setTheme(if (dark) "ace/theme/github_dark" else "ace/theme/github")
    }

    /** Ctrl/Cmd + "+"/"-" zoom the editor's font size; Ctrl/Cmd + "0" resets it, matching browser zoom shortcuts. */
    private fun handleZoomKeydown(event: KeyboardEvent) {
        if (!event.ctrlKey && !event.metaKey) return
        when (event.key) {
            "+", "=" -> setFontSize(fontSize + 1)
            "-" -> setFontSize(fontSize - 1)
            "0" -> setFontSize(EditorZoom.DEFAULT_FONT_SIZE)
            else -> return
        }
        event.preventDefault()
    }

    /** Ctrl/Cmd + mouse wheel zooms the editor instead of the whole page, mirroring native browser zoom. */
    private fun handleZoomWheel(event: WheelEvent) {
        if (!event.ctrlKey && !event.metaKey) return
        event.preventDefault()
        setFontSize(fontSize + if (event.deltaY < 0) 1 else -1)
    }

    private fun setFontSize(size: Int) {
        fontSize = EditorZoom.clamp(size)
        editor.setFontSize("${fontSize}px")
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

    /**
     * Test-only: goes through Ace's own `Editor.insert`, the exact path a real keystroke takes (auto-pairing
     * "behaviour" transforms included), unlike [value] which calls `session.setValue` and skips all of that.
     */
    internal fun testType(text: String) {
        editor.asDynamic().insert(text)
    }

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
