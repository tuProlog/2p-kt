package it.unibo.tuprolog.ui.gui

import javafx.scene.Node
import javafx.scene.control.ListCell

/**
 * A [ListCell] that delegates rendering to [viewGenerator], turning any `ListView<T>` into a view of
 * arbitrary [Node]s instead of plain text; used e.g. for the solutions list (rendered via [SolutionView.of])
 * and the warnings list.
 */
class ListCellView<T : Any>(
    private val viewGenerator: (T) -> Node,
) : ListCell<T>() {
    override fun updateItem(
        item: T?,
        empty: Boolean,
    ) {
        super.updateItem(item, empty)
        graphic =
            if (empty || item == null) {
                null
            } else {
                viewGenerator(item)
            }
    }
}
