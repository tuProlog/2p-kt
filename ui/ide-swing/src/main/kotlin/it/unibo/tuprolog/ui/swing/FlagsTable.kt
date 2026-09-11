package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor

/** Table of solver flags whose "Value" column is editable; edits are reported via [onFlagChanged]. */
internal class FlagsTable : JTable(FlagsTableModel()) {
    private val flagsModel = model as FlagsTableModel

    var onFlagChanged: ((name: String, value: String) -> Unit)?
        get() = flagsModel.onEdited
        set(value) {
            flagsModel.onEdited = value
        }

    fun render(flags: List<FlagPresentation>) {
        flagsModel.data = flags
    }

    override fun getCellEditor(
        row: Int,
        column: Int,
    ): TableCellEditor =
        flagsModel
            .notableAt(row)
            ?.takeIf { column == 1 && it.isEditable }
            ?.let { flag ->
                DefaultCellEditor(
                    JComboBox(
                        flag.admissibleValues
                            .map(Any::toString)
                            .toList()
                            .toTypedArray(),
                    ),
                )
            }
            ?: super.getCellEditor(row, column)
}

private class FlagsTableModel : AbstractTableModel() {
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
