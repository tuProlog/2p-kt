package it.unibo.tuprolog.ui.gui

import javafx.scene.Node
import javafx.scene.control.ListCell

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
