package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import javax.swing.JTable
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableRowSorter

/** Read-only, sortable table of active operators (name, priority, specifier), sorted by priority by default. */
internal class OperatorsTable : JTable(OperatorsTableModel()) {
    private val operatorsModel = model as OperatorsTableModel

    init {
        val sorter = TableRowSorter(operatorsModel)
        sorter.sortKeys = listOf(RowSorter.SortKey(1, SortOrder.ASCENDING))
        rowSorter = sorter
    }

    fun render(operators: List<OperatorPresentation>) {
        operatorsModel.data = operators
    }
}

private class OperatorsTableModel : AbstractTableModel() {
    var data: List<OperatorPresentation> = emptyList()
        set(value) {
            field = value
            fireTableDataChanged()
        }

    override fun getRowCount() = data.size

    override fun getColumnCount() = 3

    override fun getColumnName(column: Int) =
        when (column) {
            0 -> "Name"
            1 -> "Priority"
            else -> "Specifier"
        }

    override fun getColumnClass(columnIndex: Int): Class<*> =
        if (columnIndex == 1) Int::class.javaObjectType else String::class.java

    override fun getValueAt(
        rowIndex: Int,
        columnIndex: Int,
    ): Any =
        when (columnIndex) {
            0 -> data[rowIndex].name
            1 -> data[rowIndex].priority
            else -> data[rowIndex].specifier
        }

    override fun isCellEditable(
        rowIndex: Int,
        columnIndex: Int,
    ) = false
}
