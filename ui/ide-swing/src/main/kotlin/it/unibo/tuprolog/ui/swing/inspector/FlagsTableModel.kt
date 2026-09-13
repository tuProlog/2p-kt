package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import javax.swing.table.AbstractTableModel

/** Backs [FlagsTable]'s two columns ("Flag", "Value"), one row per [FlagPresentation]. */
internal class FlagsTableModel : AbstractTableModel() {
    /** Replacing this fires a full table-data-changed event. */
    var data: List<FlagPresentation> = emptyList()
        set(value) {
            field = value
            fireTableDataChanged()
        }

    /** Invoked with a flag's name and new value once [setValueAt] commits an edit. */
    var onEdited: ((String, String) -> Unit)? = null

    /** One row per known flag. */
    override fun getRowCount() = data.size

    /** Always two: "Flag" and "Value". */
    override fun getColumnCount() = 2

    /** "Flag" for column 0, "Value" for column 1. */
    override fun getColumnName(column: Int) = if (column == 0) "Flag" else "Value"

    override fun getValueAt(
        rowIndex: Int,
        columnIndex: Int,
    ): Any = if (columnIndex == 0) data[rowIndex].name else data[rowIndex].value

    /** Only the "Value" column is editable, and only for flags that aren't fixed (e.g. [NotableFlag.MaxArity]). */
    override fun isCellEditable(
        rowIndex: Int,
        columnIndex: Int,
    ) = columnIndex == 1 && (NotableFlag.fromName(data[rowIndex].name)?.isEditable != false)

    /** The [NotableFlag] at [rowIndex], if the flag shown there is a recognized one rather than an arbitrary name. */
    fun notableAt(rowIndex: Int): NotableFlag? = NotableFlag.fromName(data[rowIndex].name)

    /** Reports an edit to the "Value" column via [onEdited]; edits to "Flag" are ignored (flags aren't renamed). */
    override fun setValueAt(
        value: Any?,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        if (columnIndex != 1) return
        onEdited?.invoke(data[rowIndex].name, value?.toString().orEmpty())
    }
}
