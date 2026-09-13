package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import javax.swing.table.AbstractTableModel

/**
 * Backs [OperatorsTable]'s three columns ("Name", "Priority", "Specifier"), one row per [OperatorPresentation]
 * plus a trailing, always-editable blank row for defining a new page-local operator - committed (via [onAdded])
 * only once all three of its cells have been filled in with a parseable priority.
 */
internal class OperatorsTableModel : AbstractTableModel() {
    /** Replacing this fires a full table-data-changed event; the trailing "add" row is separate, own state. */
    var data: List<OperatorPresentation> = emptyList()
        set(value) {
            field = value
            fireTableDataChanged()
        }

    /** Invoked with the new operator once the trailing "add" row's three cells are all filled in validly. */
    var onAdded: ((OperatorPresentation) -> Unit)? = null
    private val newOperator = arrayOf("", "", "")

    /** One row per known operator, plus one trailing blank row for adding a new one. */
    override fun getRowCount() = data.size + 1

    /** Always three: "Name", "Priority", "Specifier". */
    override fun getColumnCount() = 3

    override fun getColumnName(column: Int) =
        when (column) {
            0 -> "Name"
            1 -> "Priority"
            else -> "Specifier"
        }

    /** Every column renders/edits as plain text, even "Priority" (parsed on commit, see [setValueAt]). */
    override fun getColumnClass(columnIndex: Int): Class<*> = String::class.java

    override fun getValueAt(
        rowIndex: Int,
        columnIndex: Int,
    ): Any =
        if (rowIndex == data.size) {
            newOperator[columnIndex]
        } else {
            when (columnIndex) {
                0 -> data[rowIndex].name
                1 -> data[rowIndex].priority
                else -> data[rowIndex].specifier
            }
        }

    /** Only the trailing "add" row is editable; already-added operators are read-only. */
    override fun isCellEditable(
        rowIndex: Int,
        columnIndex: Int,
    ) = rowIndex == data.size

    /**
     * Buffers an edit to the trailing "add" row's cell; once all three of its cells hold a non-blank name,
     * specifier, and a parseable integer priority, reports the completed operator via [onAdded] and clears the
     * row back to blank for the next one.
     */
    override fun setValueAt(
        value: Any?,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        if (rowIndex != data.size) return
        newOperator[columnIndex] = value?.toString().orEmpty().trim()
        fireTableCellUpdated(rowIndex, columnIndex)
        val (name, priority, specifier) = newOperator
        priority.toIntOrNull()?.let {
            if (name.isNotBlank() && specifier.isNotBlank()) {
                onAdded?.invoke(OperatorPresentation(name, it, specifier))
                newOperator.fill("")
                fireTableRowsUpdated(rowIndex, rowIndex)
            }
        }
    }
}
