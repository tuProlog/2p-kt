package it.unibo.tuprolog.ui.swing

internal sealed interface SolutionNodeData {
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
