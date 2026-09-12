package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import javax.swing.table.AbstractTableModel

internal class FlagsTableModel : AbstractTableModel() {
    var data: List<FlagPresentation> = emptyList()
        set(value) {
            field = value
            fireTableDataChanged()
        }
    var onEdited: ((String, String) -> Unit)? = null

    override fun getRowCount() = data.size

    override fun getColumnCount() = 2

    override fun getColumnName(column: Int) = if (column == 0) "Flag" else "Value"

    override fun getValueAt(
        rowIndex: Int,
        columnIndex: Int,
    ): Any = if (columnIndex == 0) data[rowIndex].name else data[rowIndex].value

    override fun isCellEditable(
        rowIndex: Int,
        columnIndex: Int,
    ) = columnIndex == 1 && (NotableFlag.fromName(data[rowIndex].name)?.isEditable != false)

    fun notableAt(rowIndex: Int): NotableFlag? = NotableFlag.fromName(data[rowIndex].name)

    override fun setValueAt(
        value: Any?,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        if (columnIndex != 1) return
        onEdited?.invoke(data[rowIndex].name, value?.toString().orEmpty())
    }
}
