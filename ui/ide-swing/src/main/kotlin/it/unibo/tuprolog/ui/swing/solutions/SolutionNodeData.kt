package it.unibo.tuprolog.ui.swing.solutions

/** The payload a [SolutionTree] node's `userObject` carries, read by [SolutionCellRenderer] to pick its label/icon. */
internal sealed interface SolutionNodeData {
    /** A submitted query, shown as its own top-level row with its solutions/details nested under it. */
    data class QueryNode(
        val query: String,
    ) : SolutionNodeData {
        override fun toString() = "?- $query"
    }

    /** One resolved solution (yes/no/halt) of the enclosing [QueryNode]'s query. */
    data class ResultNode(
        val label: String,
        val kind: ResultKind,
    ) : SolutionNodeData {
        override fun toString() = label
    }

    /** A leaf under a [ResultNode]: one variable binding, or one line of a halted solution's stack trace. */
    data class DetailNode(
        val text: String,
    ) : SolutionNodeData {
        override fun toString() = text
    }

    /** Trailing "…" row shown under a [QueryNode] whose query may still have unexplored solutions. */
    data object EllipsisNode : SolutionNodeData {
        override fun toString() = "…"
    }
}
