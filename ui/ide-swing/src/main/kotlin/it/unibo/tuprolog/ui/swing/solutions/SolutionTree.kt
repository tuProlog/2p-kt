package it.unibo.tuprolog.ui.swing.solutions

import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

/** Shows every submitted query, grouped with its solutions and their bindings/stack traces, as a tree. */
internal class SolutionTree : JTree(DefaultMutableTreeNode("Solutions")) {
    /** Invoked with a query's text when the user selects (any row under) that query. */
    var onQuerySelected: ((String) -> Unit)? = null
    private var updating = false

    init {
        isRootVisible = false
        showsRootHandles = true
        // setting protected method directly would not work, must use setter
        setCellRenderer(SolutionCellRenderer())
        addTreeSelectionListener {
            if (updating) return@addTreeSelectionListener
            val node = lastSelectedPathComponent as? DefaultMutableTreeNode
            val query = node?.ancestorQuery() ?: return@addTreeSelectionListener
            onQuerySelected?.invoke(query)
        }
    }

    /** Rebuilds the tree from [entries], expanding and scrolling to [focusedQuery]'s node if given. */
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
                        "$number. yes: ${solution.solvedQuery ?: solution.query}${probabilityAnnotation(solution)}",
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
