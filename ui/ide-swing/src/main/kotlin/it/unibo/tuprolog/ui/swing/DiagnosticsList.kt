package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import java.awt.Color
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList

internal class DiagnosticsList : JList<Diagnostic>(DefaultListModel()) {
    var onDiagnosticSelected: ((Diagnostic) -> Unit)? = null

    init {
        cellRenderer = DiagnosticCellRenderer()
        addListSelectionListener { event ->
            if (event.valueIsAdjusting) return@addListSelectionListener
            selectedValue?.let { onDiagnosticSelected?.invoke(it) }
        }
    }

    fun render(diagnostics: List<Diagnostic>) {
        val listModel = DefaultListModel<Diagnostic>()
        diagnostics.forEach(listModel::addElement)
        model = listModel
    }
}

private class DiagnosticCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean,
    ): Component {
        val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
        val diagnostic = value as? Diagnostic ?: return label
        // Zero-based, like TextPosition itself and the line:column already embedded in parser error messages.
        val location =
            diagnostic.range
                ?.start
                ?.let { " (line ${it.line}, column ${it.column})" }
                .orEmpty()
        label.text = "${diagnostic.severity}: ${diagnostic.message}$location"
        if (!isSelected) {
            label.foreground =
                when (diagnostic.severity) {
                    DiagnosticSeverity.ERROR -> ERROR_COLOR
                    DiagnosticSeverity.WARNING -> WARNING_COLOR
                    DiagnosticSeverity.INFO -> INFO_COLOR
                }
        }
        return label
    }

    private companion object {
        val ERROR_COLOR = Color(0xC6, 0x28, 0x28)
        val WARNING_COLOR = Color(0xE6, 0x8A, 0x00)
        val INFO_COLOR = Color(0x15, 0x65, 0xC0)
    }
}
