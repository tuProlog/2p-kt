package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
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
