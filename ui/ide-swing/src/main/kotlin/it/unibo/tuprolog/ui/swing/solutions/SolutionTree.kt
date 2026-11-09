package it.unibo.tuprolog.ui.swing.solutions

import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.detailLines
import it.unibo.tuprolog.ui.gui.presentation.logicText
import it.unibo.tuprolog.ui.gui.presentation.resultLabel
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JTree
import javax.swing.KeyStroke
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

/** Shows every submitted query, grouped with its solutions and their bindings/stack traces, as a tree. */
internal class SolutionTree : JTree(DefaultMutableTreeNode("Solutions")) {
    /** Invoked with a query's text when the user selects (any row under) that query. */
    var onQuerySelected: ((String) -> Unit)? = null
    var onDeleteQuery: ((ResolutionHistoryEntry) -> Unit)? = null
    var onSolveQuery: ((String) -> Unit)? = null
    private var renderedEntries: List<SolutionQueryEntry>? = null
    private var renderedFocus: String? = null
    private var updating = false

    init {
        isRootVisible = false
        showsRootHandles = true
        // setting protected method directly would not work, must use setter
        setCellRenderer(SolutionCellRenderer())
        addMouseListener(
            object : MouseAdapter() {
                override fun mousePressed(event: MouseEvent) = showPopup(event)

                override fun mouseReleased(event: MouseEvent) = showPopup(event)
            },
        )
        inputMap.put(KeyStroke.getKeyStroke("shift F10"), "solutionMenu")
        inputMap.put(KeyStroke.getKeyStroke("CONTEXT_MENU"), "solutionMenu")
        actionMap.put(
            "solutionMenu",
            object : AbstractAction() {
                override fun actionPerformed(event: ActionEvent) {
                    val path = selectionPath ?: return
                    val bounds = getPathBounds(path) ?: return
                    popupFor(path).show(this@SolutionTree, bounds.x, bounds.y + bounds.height)
                }
            },
        )
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
        if (entries == renderedEntries && focusedQuery == renderedFocus) return
        renderedEntries = entries
        renderedFocus = focusedQuery
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

    private fun showPopup(event: MouseEvent) {
        if (!event.isPopupTrigger) return
        val path = getPathForLocation(event.x, event.y) ?: return
        updating = true
        try {
            selectionPath = path
        } finally {
            updating = false
        }
        popupFor(path).show(this, event.x, event.y)
    }

    internal fun popupFor(path: TreePath): JPopupMenu {
        val node = path.lastPathComponent as DefaultMutableTreeNode
        val data = node.userObject
        return JPopupMenu().apply {
            fun item(
                label: String,
                id: String,
                enabled: Boolean = true,
                action: () -> Unit,
            ) {
                add(
                    JMenuItem(label).apply {
                        name = id
                        isEnabled = enabled
                        addActionListener { action() }
                    },
                )
            }
            item("Copy full text", "copySolutionText") { copyText(node.fullText()) }
            item("Copy logic text", "copySolutionLogic", data != SolutionNodeData.EllipsisNode) {
                copyText(
                    when (data) {
                        is SolutionNodeData.QueryNode -> data.query
                        is SolutionNodeData.ResultNode -> data.logicText
                        else -> data.toString()
                    },
                )
            }
            if (data is SolutionNodeData.QueryNode) {
                addSeparator()
                item("Delete", "deleteSolutionQuery", data.historyEntry != null) {
                    data.historyEntry?.let { onDeleteQuery?.invoke(it) }
                }
                item("Solve this", "solveSolutionQuery") { onSolveQuery?.invoke(data.query) }
            }
            if (!node.isLeaf) {
                addSeparator()
                item(if (isExpanded(path)) "Collapse" else "Expand", "toggleSolutionNode") {
                    if (isExpanded(path)) collapsePath(path) else expandPath(path)
                }
            }
        }
    }

    private fun copyText(text: String) {
        try {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
        } catch (exception: IllegalStateException) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                exception.message,
                "Cannot access clipboard",
                javax.swing.JOptionPane.ERROR_MESSAGE,
            )
        }
    }

    private fun DefaultMutableTreeNode.fullText(indent: String = ""): String =
        indent + userObject.toString() +
            children()
                .asSequence()
                .filterIsInstance<DefaultMutableTreeNode>()
                .joinToString("") { "\n" + it.fullText("$indent  ") }

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
        DefaultMutableTreeNode(
            SolutionNodeData.QueryNode(
                entry.query,
                entry.submittedAt,
                entry.historyEntry,
            ),
        ).apply {
            entry.solutions.forEachIndexed { index, solution -> add(solutionNode(index + 1, solution)) }
            if (entry.hasUnexploredPaths) add(DefaultMutableTreeNode(SolutionNodeData.EllipsisNode))
        }

    private fun solutionNode(
        number: Int,
        solution: SolutionPresentation,
    ): DefaultMutableTreeNode =
        DefaultMutableTreeNode(
            SolutionNodeData.ResultNode(
                solution.resultLabel(number),
                when (solution) {
                    is SolutionPresentation.Yes -> ResultKind.YES
                    is SolutionPresentation.No -> ResultKind.NO
                    is SolutionPresentation.Halt -> ResultKind.HALT
                },
                solution.logicText,
            ),
        ).apply {
            solution.detailLines.forEach { add(DefaultMutableTreeNode(SolutionNodeData.DetailNode(it))) }
        }
}
