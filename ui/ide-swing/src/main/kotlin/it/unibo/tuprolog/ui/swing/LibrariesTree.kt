package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/** One node per loaded library, grouping its predicate/function/operator signatures. */
internal class LibrariesTree : JTree(DefaultMutableTreeNode("Libraries")) {
    init {
        isRootVisible = false
        showsRootHandles = true
    }

    fun render(libraries: List<LibraryPresentation>) {
        val root = DefaultMutableTreeNode("Libraries")
        libraries.forEach { root.add(libraryNode(it)) }
        model = DefaultTreeModel(root)
        for (row in 0 until rowCount) expandRow(row)
    }

    private fun libraryNode(library: LibraryPresentation): DefaultMutableTreeNode =
        DefaultMutableTreeNode(library.alias).apply {
            if (library.predicates.isNotEmpty()) add(groupNode("Predicates", library.predicates))
            if (library.functions.isNotEmpty()) add(groupNode("Functions", library.functions))
            if (library.operators.isNotEmpty()) {
                add(
                    groupNode(
                        "Operators",
                        library.operators.map { "${it.name} (${it.specifier}, priority ${it.priority})" },
                    ),
                )
            }
        }

    private fun groupNode(
        title: String,
        items: List<String>,
    ): DefaultMutableTreeNode =
        DefaultMutableTreeNode(title).apply { items.forEach { add(DefaultMutableTreeNode(it)) } }
}
