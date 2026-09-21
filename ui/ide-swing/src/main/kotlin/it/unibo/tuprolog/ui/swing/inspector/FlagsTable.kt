package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.solve.flags.FlagDomain
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import org.gciatto.kt.math.BigInteger
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JSpinner
import javax.swing.JTable
import javax.swing.SpinnerNumberModel
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

    /** For a [it.unibo.tuprolog.solve.flags.NotableFlag], edits it with a widget matching its
     * [FlagDomain] -- a bounded spinner for an [FlagDomain.IntRange], a dropdown of the legal values
     * otherwise -- rather than the default free-text editor. */
    override fun getCellEditor(
        row: Int,
        column: Int,
    ): TableCellEditor =
        flagsModel
            .notableAt(row)
            ?.takeIf { column == 1 && it.isEditable }
            ?.let { flag ->
                when (val domain = flag.admissibleValues) {
                    is FlagDomain.IntRange -> {
                        val min = domain.minInclusive.toIntClamped()
                        val max = domain.maxInclusive.toIntClamped()
                        val current = getValueAt(row, column).toString().toIntOrNull()?.coerceIn(min, max) ?: min
                        SpinnerCellEditor(SpinnerNumberModel(current, min, max, 1))
                    }
                    else -> DefaultCellEditor(JComboBox(domain.map(Any::toString).toTypedArray()))
                }
            }
            ?: super.getCellEditor(row, column)

    /** Wraps a [JSpinner] as a [TableCellEditor], for flags whose domain is a [FlagDomain.IntRange]. */
    private class SpinnerCellEditor(
        model: SpinnerNumberModel,
    ) : AbstractCellEditor(),
        TableCellEditor {
        private val spinner = JSpinner(model)

        override fun getCellEditorValue(): Any = spinner.value

        override fun getTableCellEditorComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            row: Int,
            column: Int,
        ): Component {
            value?.toString()?.toIntOrNull()?.let { spinner.value = it }
            return spinner
        }
    }
}

/** Narrows this [BigInteger] to an [Int], clamping to [Int.MIN_VALUE]/[Int.MAX_VALUE] instead of wrapping
 * around, since [JSpinner]/[SpinnerNumberModel] only support `Int` bounds while [FlagDomain.IntRange] allows
 * arbitrary-precision ones. */
internal fun BigInteger.toIntClamped(): Int =
    when {
        this > BigInteger.of(Int.MAX_VALUE) -> Int.MAX_VALUE
        this < BigInteger.of(Int.MIN_VALUE) -> Int.MIN_VALUE
        else -> toInt()
    }
