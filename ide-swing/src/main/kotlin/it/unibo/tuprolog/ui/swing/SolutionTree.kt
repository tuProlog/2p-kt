package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import java.awt.Color
import java.awt.Component
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Icon
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

/** One raw query submitted on the current page, alongside the solutions it has produced so far. */
internal data class SolutionQueryEntry(
    val query: String,
    val solutions: List<SolutionPresentation>,
    val hasUnexploredPaths: Boolean,
)

private sealed interface SolutionNodeData {
    data class QueryNode(
        val query: String,
    ) : SolutionNodeData {
        override fun toString() = "?- $query"
    }

    data class ResultNode(
        val label: String,
        val color: Color,
    ) : SolutionNodeData {
        override fun toString() = label
    }

    data class DetailNode(
        val text: String,
    ) : SolutionNodeData {
        override fun toString() = text
    }

    data object EllipsisNode : SolutionNodeData {
        override fun toString() = "…"
    }
}

internal class SolutionTree : JTree(DefaultMutableTreeNode("Solutions")) {
    var onQuerySelected: ((String) -> Unit)? = null
    private var updating = false

    init {
        isRootVisible = false
        showsRootHandles = true
        cellRenderer = SolutionCellRenderer()
        addTreeSelectionListener {
            if (updating) return@addTreeSelectionListener
            val node = lastSelectedPathComponent as? DefaultMutableTreeNode
            val data = node?.userObject as? SolutionNodeData.QueryNode ?: return@addTreeSelectionListener
            onQuerySelected?.invoke(data.query)
        }
    }

    fun render(entries: List<SolutionQueryEntry>) {
        updating = true
        try {
            val root = DefaultMutableTreeNode("Solutions")
            entries.forEach { root.add(queryNode(it)) }
            model = DefaultTreeModel(root)
            for (row in 0 until rowCount) expandRow(row)
        } finally {
            updating = false
        }
    }

    private fun queryNode(entry: SolutionQueryEntry): DefaultMutableTreeNode =
        DefaultMutableTreeNode(SolutionNodeData.QueryNode(entry.query)).apply {
            entry.solutions.forEachIndexed { index, solution -> add(solutionNode(index + 1, solution)) }
            if (entry.hasUnexploredPaths) add(DefaultMutableTreeNode(SolutionNodeData.EllipsisNode))
        }

    private fun solutionNode(
        number: Int,
        solution: SolutionPresentation,
    ): DefaultMutableTreeNode =
        when (solution) {
            is SolutionPresentation.Yes ->
                DefaultMutableTreeNode(
                    SolutionNodeData.ResultNode("$number. ${solution.solvedQuery ?: solution.query}", YES_COLOR),
                ).apply {
                    solution.bindings.forEach {
                        add(DefaultMutableTreeNode(SolutionNodeData.DetailNode("${it.variable} = ${it.value}")))
                    }
                }
            is SolutionPresentation.No ->
                DefaultMutableTreeNode(SolutionNodeData.ResultNode("$number. no", NO_COLOR))
            is SolutionPresentation.Halt ->
                DefaultMutableTreeNode(
                    SolutionNodeData.ResultNode(
                        "$number. ${if (solution.isTimeout) "timeout" else "halt"}: ${solution.message}",
                        HALT_COLOR,
                    ),
                ).apply {
                    solution.logicStackTrace.forEach {
                        add(
                            DefaultMutableTreeNode(SolutionNodeData.DetailNode("at $it")),
                        )
                    }
                }
        }

    private companion object {
        val YES_COLOR = Color(0x2E, 0x7D, 0x32)
        val NO_COLOR = Color(0xC6, 0x28, 0x28)
        val HALT_COLOR = Color(0xE6, 0x8A, 0x00)
    }
}

private class SolutionCellRenderer : DefaultTreeCellRenderer() {
    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean,
    ): Component {
        val data = (value as? DefaultMutableTreeNode)?.userObject
        val label =
            when (data) {
                is SolutionNodeData.QueryNode -> "?- ${data.query}"
                is SolutionNodeData.ResultNode -> data.label
                is SolutionNodeData.DetailNode -> data.text
                SolutionNodeData.EllipsisNode -> "…"
                else -> value.toString()
            }
        super.getTreeCellRendererComponent(tree, label, selected, expanded, leaf, row, hasFocus)
        icon = (data as? SolutionNodeData.ResultNode)?.let { DotIcon(it.color) }
        font = font.deriveFont(if (data is SolutionNodeData.EllipsisNode) Font.ITALIC else Font.PLAIN)
        return this
    }
}

private class DotIcon(
    private val color: Color,
) : Icon {
    override fun getIconWidth() = 10

    override fun getIconHeight() = 10

    override fun paintIcon(
        component: Component?,
        graphics: Graphics,
        x: Int,
        y: Int,
    ) {
        val g2 = graphics.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = color
            g2.fillOval(x, y + 2, iconWidth, iconHeight)
        } finally {
            g2.dispose()
        }
    }
}
