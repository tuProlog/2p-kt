package it.unibo.tuprolog.solve.problogclassic.knowledge

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.struct.BinaryDecisionDiagram

/**
 * This represents a solved ground term involved in the resolution of a probabilistic logic query.
 * This is used in the goal resolution process to represent boolean variables in the solution's [BinaryDecisionDiagram].
 *
 * @author Jason Dellaluce
 * */
data class ProblogSolutionTerm(
    val clauseId: Long,
    val probability: Double,
    val term: Term,
) : Comparable<ProblogSolutionTerm> {
    override fun compareTo(other: ProblogSolutionTerm): Int {
        var res = this.clauseId.compareTo(other.clauseId)
        if (res == 0) {
            res = this.term.compareTo(other.term)
        }
        return res
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is ProblogSolutionTerm) false else this.compareTo(other) == 0
    }

    override fun toString(): String {
        return "[$clauseId] $probability::$term"
    }

    override fun hashCode(): Int {
        var result = clauseId.hashCode()
        result = 31 * result + probability.hashCode()
        result = 31 * result + term.hashCode()
        return result
    }
}
