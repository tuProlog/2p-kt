package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import javax.swing.DefaultListModel
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
