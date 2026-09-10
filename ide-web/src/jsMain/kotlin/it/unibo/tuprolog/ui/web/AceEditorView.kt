package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.web.ace.Ace
import it.unibo.tuprolog.ui.web.ace.AceEditor
import it.unibo.tuprolog.ui.web.ace.aceAnnotation
import org.w3c.dom.HTMLElement

/** Thin, Kotlin-friendly adapter around the [Ace] editor, hiding its JS-shaped API from the rest of the view. */
internal class AceEditorView(
    container: HTMLElement,
) {
    private val editor: AceEditor = Ace.edit(container)

    init {
        editor.setTheme("ace/theme/github")
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
}
