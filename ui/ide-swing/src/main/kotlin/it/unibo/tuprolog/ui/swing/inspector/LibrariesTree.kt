package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation
import it.unibo.tuprolog.ui.gui.presentation.documentationPreview
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * One node per loaded library, grouping its predicate/function/operator signatures. Documented entries show a
 * preview of their documentation; double-clicking one opens a window with the rendered documentation.
 */
internal class LibrariesTree : JTree(DefaultMutableTreeNode("Libraries")) {
    /** A tree entry labelled [label], documented by [markdown] (`null` or blank if undocumented). */
    data class Entry(
        val label: String,
        val markdown: String?,
    ) {
        override fun toString(): String =
            markdown?.let(::documentationPreview)?.takeIf { it.isNotEmpty() }?.let { "$label — $it" } ?: label
    }

    init {
        isRootVisible = false
        showsRootHandles = true
        addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount != 2) return
                    val node = getPathForLocation(e.x, e.y)?.lastPathComponent as? DefaultMutableTreeNode
                    (node?.userObject as? Entry)?.let {
                        showDocumentationWindow(
                            this@LibrariesTree,
                            it.label,
                            it.markdown,
                        )
                    }
                }
            },
        )
    }

    /** Rebuilds the tree, fully expanded, with one top-level node per library in [libraries]. */
    fun render(libraries: List<LibraryPresentation>) {
        val root = DefaultMutableTreeNode("Libraries")
        libraries.forEach { root.add(libraryNode(it)) }
        model = DefaultTreeModel(root)
        for (row in 0 until rowCount) expandRow(row)
    }

    private fun libraryNode(library: LibraryPresentation): DefaultMutableTreeNode =
        DefaultMutableTreeNode(Entry(library.alias, library.help)).apply {
            if (library.predicates.isNotEmpty()) add(groupNode("Predicates", library.predicates, library))
            if (library.functions.isNotEmpty()) add(groupNode("Functions", library.functions, library))
            if (library.operators.isNotEmpty()) {
                add(
                    DefaultMutableTreeNode("Operators").apply {
                        library.operators.forEach {
                            val label = "${it.name} (${it.specifier}, priority ${it.priority})"
                            add(DefaultMutableTreeNode(Entry(label, library.documentationOf(it))))
                        }
                    },
                )
            }
        }

    private fun groupNode(
        title: String,
        items: List<String>,
        library: LibraryPresentation,
    ): DefaultMutableTreeNode =
        DefaultMutableTreeNode(title).apply {
            items.forEach { add(DefaultMutableTreeNode(Entry(it, library.documentation[it]))) }
        }
}
