package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.TableCellEditor

/** Table of solver flags whose "Value" column is editable; edits are reported via [onFlagChanged]. */
internal class FlagsTable : JTable(FlagsTableModel()) {
    private val flagsModel = model as FlagsTableModel

    /** Invoked with a flag's name and new value once the user commits an edit to the "Value" column. */
    var onFlagChanged: ((name: String, value: String) -> Unit)?
        get() = flagsModel.onEdited
        set(value) {
            flagsModel.onEdited = value
        }

    /** Replaces the displayed flags with [flags]. */
    fun render(flags: List<FlagPresentation>) {
        flagsModel.data = flags
    }

    /** For a [it.unibo.tuprolog.solve.flags.NotableFlag] with a fixed set of admissible values, edits it via a
     * dropdown of those values rather than the default free-text editor. */
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
