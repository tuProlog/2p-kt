package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import javax.swing.JTable
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.SwingConstants
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableRowSorter

/** Sortable table of active operators, with a final row for adding a page-local operator directive. */
internal class OperatorsTable : JTable(OperatorsTableModel()) {
    private val operatorsModel = model as OperatorsTableModel

    /** Invoked with the new operator once the user fills in and commits the table's trailing "add" row. */
    var onOperatorAdded: ((OperatorPresentation) -> Unit)?
        get() = operatorsModel.onAdded
        set(value) {
            operatorsModel.onAdded = value
        }

    init {
        val sorter = TableRowSorter(operatorsModel)
        sorter.sortKeys = listOf(RowSorter.SortKey(1, SortOrder.ASCENDING))
        sorter.setComparator(
            1,
            Comparator<Any> { left, right ->
                val leftPriority = left.toString().toIntOrNull() ?: Int.MAX_VALUE
                val rightPriority = right.toString().toIntOrNull() ?: Int.MAX_VALUE
                leftPriority.compareTo(rightPriority)
            },
        )
        rowSorter = sorter
        setDefaultRenderer(
            String::class.java,
            DefaultTableCellRenderer().apply {
                horizontalAlignment =
                    SwingConstants.CENTER
            },
        )
        setDefaultRenderer(
            Int::class.javaObjectType,
            DefaultTableCellRenderer().apply {
                horizontalAlignment =
                    SwingConstants.CENTER
            },
        )
    }

    /** Replaces the displayed operators with [operators] (the trailing "add" row is separate, model-owned state). */
    fun render(operators: List<OperatorPresentation>) {
        operatorsModel.data = operators
    }
}
