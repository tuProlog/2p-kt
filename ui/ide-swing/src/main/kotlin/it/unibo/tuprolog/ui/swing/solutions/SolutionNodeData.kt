package it.unibo.tuprolog.ui.swing.solutions

import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.presentation.queryLabel

/** The payload a [SolutionTree] node's `userObject` carries, read by [SolutionCellRenderer] to pick its label/icon. */
internal sealed interface SolutionNodeData {
    /**
     * A submitted query with its local submission timestamp and nested solutions/details.
     */
    data class QueryNode(
        val query: String,
        val submittedAt: Long? = null,
        val historyEntry: ResolutionHistoryEntry? = null,
    ) : SolutionNodeData {
        override fun toString() = queryLabel(query, submittedAt)
    }

    /** One resolved solution (yes/no/halt) of the enclosing [QueryNode]'s query. */
    data class ResultNode(
        val label: String,
        val kind: ResultKind,
        val logicText: String = label,
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
