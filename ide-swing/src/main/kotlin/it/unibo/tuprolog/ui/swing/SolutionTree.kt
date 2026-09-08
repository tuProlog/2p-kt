package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

internal class SolutionTree : JTree(DefaultMutableTreeNode("Solutions")) {
    init {
        isRootVisible = false
        showsRootHandles = true
    }

    fun render(solutions: List<SolutionPresentation>) {
        val root = DefaultMutableTreeNode("Solutions")
        solutions.forEachIndexed { index, solution -> root.add(solutionNode(index + 1, solution)) }
        model = DefaultTreeModel(root)
        for (row in 0 until rowCount) expandRow(row)
    }

    private fun solutionNode(
        number: Int,
        solution: SolutionPresentation,
    ): DefaultMutableTreeNode =
        when (solution) {
            is SolutionPresentation.Yes ->
                DefaultMutableTreeNode("$number. yes").apply {
                    solution.solvedQuery?.let { add(DefaultMutableTreeNode("Query: $it")) }
                    solution.bindings.forEach { add(DefaultMutableTreeNode("${it.variable} = ${it.value}")) }
                }
            is SolutionPresentation.No -> DefaultMutableTreeNode("$number. no")
            is SolutionPresentation.Halt ->
                DefaultMutableTreeNode(
                    "$number. ${if (solution.isTimeout) "timeout" else "halt"}: ${solution.message}",
                ).apply {
                    solution.logicStackTrace.forEach { add(DefaultMutableTreeNode("at $it")) }
                }
        }
}
