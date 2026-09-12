package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import javax.swing.table.AbstractTableModel

internal class OperatorsTableModel : AbstractTableModel() {
    var data: List<OperatorPresentation> = emptyList()
        set(value) {
            field = value
            fireTableDataChanged()
        }

    var onAdded: ((OperatorPresentation) -> Unit)? = null
    private val newOperator = arrayOf("", "", "")

    override fun getRowCount() = data.size + 1

    override fun getColumnCount() = 3

    override fun getColumnName(column: Int) =
        when (column) {
            0 -> "Name"
            1 -> "Priority"
            else -> "Specifier"
        }

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

    override fun isCellEditable(
        rowIndex: Int,
        columnIndex: Int,
    ) = rowIndex == data.size

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
