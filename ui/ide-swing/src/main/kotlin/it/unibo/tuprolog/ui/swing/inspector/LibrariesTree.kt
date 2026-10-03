package it.unibo.tuprolog.ui.swing.inspector

import it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation
import it.unibo.tuprolog.ui.gui.presentation.documentationPreview
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

/**
 * One node per loaded library, grouping its predicate/function/operator signatures. Documented entries show a
 * one-line preview of their documentation, truncated to the available width; double-clicking one opens a window
 * with the rendered documentation.
 */
internal class LibrariesTree : JTree(DefaultMutableTreeNode("Libraries")) {
    /** A tree entry labelled [label], documented by [markdown] (`null` or blank if undocumented). */
    data class Entry(
        val label: String,
        val markdown: String?,
    ) {
        override fun toString(): String =
            markdown
                ?.let { documentationPreview(it, maxLength = Int.MAX_VALUE) }
                ?.takeIf { it.isNotEmpty() }
                ?.let { "$label — $it" } ?: label
    }

    /** Stretches each row to the tree's width, so that the label is truncated (with `…`) rather than overflowing. */
    private class FullWidthRenderer : DefaultTreeCellRenderer() {
        private var availableWidth = 0

        override fun getTreeCellRendererComponent(
            tree: JTree,
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean,
        ): Component {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
            // rows are indented by one step per level below the invisible root's children, which sit at the left edge
            val indent = (tree.ui as? BasicTreeUI)?.let { it.leftChildIndent + it.rightChildIndent } ?: 0
            val depth = ((value as? DefaultMutableTreeNode)?.level ?: 1) - 1
            availableWidth = tree.width - tree.insets.left - tree.insets.right - indent * depth
            return this
        }

        override fun getPreferredSize(): Dimension =
            super.getPreferredSize().let { if (availableWidth > 0) Dimension(availableWidth, it.height) else it }
    }

    override fun getScrollableTracksViewportWidth(): Boolean = true

    init {
        isRootVisible = false
        showsRootHandles = true
        setCellRenderer(FullWidthRenderer())
        addComponentListener(
            object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    // re-setting an indent is the public way to make the tree UI drop its cached row sizes
                    (ui as? BasicTreeUI)?.let { it.setLeftChildIndent(it.leftChildIndent) }
                }
            },
        )
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
