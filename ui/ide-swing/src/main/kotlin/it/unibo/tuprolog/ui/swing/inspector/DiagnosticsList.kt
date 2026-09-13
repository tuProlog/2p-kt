package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import javax.swing.DefaultListModel
import javax.swing.JList

/** Lists parser/analyzer [Diagnostic]s for the current page, reporting clicks via [onDiagnosticSelected]. */
internal class DiagnosticsList : JList<Diagnostic>(DefaultListModel()) {
    /** Invoked (not while the selection is still being dragged) with the diagnostic the user picked. */
    var onDiagnosticSelected: ((Diagnostic) -> Unit)? = null

    init {
        cellRenderer = DiagnosticCellRenderer()
        addListSelectionListener { event ->
            if (event.valueIsAdjusting) return@addListSelectionListener
            selectedValue?.let { onDiagnosticSelected?.invoke(it) }
        }
    }

    /** Replaces the displayed diagnostics with [diagnostics]. */
    fun render(diagnostics: List<Diagnostic>) {
        val listModel = DefaultListModel<Diagnostic>()
        diagnostics.forEach(listModel::addElement)
        model = listModel
    }
}
