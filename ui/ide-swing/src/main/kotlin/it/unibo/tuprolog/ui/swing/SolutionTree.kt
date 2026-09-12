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
import javax.swing.tree.TreePath

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
        val kind: ResultKind,
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

private enum class ResultKind { YES, NO, HALT }

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
            val query = node?.ancestorQuery() ?: return@addTreeSelectionListener
            onQuerySelected?.invoke(query)
        }
    }

    fun render(
        entries: List<SolutionQueryEntry>,
        focusedQuery: String? = null,
    ) {
        updating = true
        try {
            val root = DefaultMutableTreeNode("Solutions")
            entries.forEach { root.add(queryNode(it)) }
            model = DefaultTreeModel(root)
            for (row in rowCount - 1 downTo 0) collapseRow(row)
            root.children().asSequence().filterIsInstance<DefaultMutableTreeNode>().forEach { node ->
                val path = TreePath(node.path)
                if ((node.userObject as? SolutionNodeData.QueryNode)?.query == focusedQuery) {
                    expandRecursively(path)
                    scrollPathToVisible(path.lastDescendantPath())
                }
            }
        } finally {
            updating = false
        }
    }

    private fun expandRecursively(path: TreePath) {
        expandPath(path)
        val node = path.lastPathComponent as DefaultMutableTreeNode
        node.children().asSequence().filterIsInstance<DefaultMutableTreeNode>().forEach { child ->
            expandRecursively(path.pathByAddingChild(child))
        }
    }

    private fun TreePath.lastDescendantPath(): TreePath {
        val node = lastPathComponent as DefaultMutableTreeNode
        if (node.childCount == 0) return this
        // getLastChild() throws NoSuchElementException on a childless node, hence the count check above.
        val lastChild = node.lastChild as? DefaultMutableTreeNode
        return if (lastChild == null) this else pathByAddingChild(lastChild).lastDescendantPath()
    }

    private fun DefaultMutableTreeNode.ancestorQuery(): String? =
        generateSequence(this) { it.parent as? DefaultMutableTreeNode }
            .mapNotNull { (it.userObject as? SolutionNodeData.QueryNode)?.query }
            .firstOrNull()

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
                    SolutionNodeData.ResultNode(
                        "$number. ${solution.solvedQuery ?: solution.query}${probabilityAnnotation(solution)}",
                        ResultKind.YES,
                    ),
                ).apply {
                    solution.bindings.forEach {
                        add(DefaultMutableTreeNode(SolutionNodeData.DetailNode("${it.variable} = ${it.value}")))
                    }
                }
            is SolutionPresentation.No ->
                DefaultMutableTreeNode(SolutionNodeData.ResultNode("$number. no", ResultKind.NO))
            is SolutionPresentation.Halt ->
                DefaultMutableTreeNode(
                    SolutionNodeData.ResultNode(
                        "$number. ${if (solution.isTimeout) "timeout" else "halt"}: ${solution.message}",
                        ResultKind.HALT,
                    ),
                ).apply {
                    solution.logicStackTrace.forEach {
                        add(
                            DefaultMutableTreeNode(SolutionNodeData.DetailNode("at $it")),
                        )
                    }
                }
        }

    /**
     * A trailing " (p=42%)"-style annotation when the solution carries a numeric `"probability"` entry in its
     * [SolutionPresentation.Yes.metadata] (populated by profiles like PLP's, see `Solution.toStep` in
     * gui-prolog) - so a probabilistic solver's per-solution probability shows up right next to each solution
     * in this shared tree, without ide-swing needing any PLP-specific dependency to read it.
     */
    private fun probabilityAnnotation(solution: SolutionPresentation.Yes): String =
        solution.metadata["probability"]
            ?.toDoubleOrNull()
            ?.let { " (p=%.1f%%)".format(it * PERCENT) }
            .orEmpty()

    private companion object {
        const val PERCENT = 100.0
    }
}

private class SolutionCellRenderer : DefaultTreeCellRenderer() {
    // Some look-and-feels (observed with Aqua on macOS) paint a JTree's open/closed/leaf icon by calling the
    // renderer's own getOpenIcon()/getClosedIcon()/getLeafIcon() bean getters instead of (only) using the icon
    // set on the component getTreeCellRendererComponent() returns, silently substituting their native
    // folder/document glyphs otherwise (see https://stackoverflow.com/a/38994868). Overriding those getters to
    // echo back whatever icon was just computed for the current row keeps both painting paths in sync.
    private var currentIcon: Icon? = null

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
        currentIcon =
            when (data) {
                is SolutionNodeData.QueryNode -> Icons.QUERY
                is SolutionNodeData.ResultNode -> resultIcon(data.kind)
                is SolutionNodeData.DetailNode -> DotIcon(DETAIL_COLOR)
                else -> null
            }
        icon = currentIcon
        font = font.deriveFont(if (data is SolutionNodeData.EllipsisNode) Font.ITALIC else Font.PLAIN)
        return this
    }

    override fun getLeafIcon(): Icon? = currentIcon

    override fun getOpenIcon(): Icon? = currentIcon

    override fun getClosedIcon(): Icon? = currentIcon

    private fun resultIcon(kind: ResultKind): Icon =
        when (kind) {
            ResultKind.YES -> Icons.YES_SOLUTION
            ResultKind.NO -> Icons.NO_SOLUTION
            ResultKind.HALT -> Icons.HALT_SOLUTION
        }

    private companion object {
        val DETAIL_COLOR = Color(0x90, 0x90, 0x90)
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
