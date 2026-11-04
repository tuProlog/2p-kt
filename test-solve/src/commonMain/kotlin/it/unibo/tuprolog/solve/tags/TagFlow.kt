package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.core.Term

/**
 * Records how tags flow during resolution: every observed cross-contamination between the tags originated on the goal
 * side (expected on the left of every comparison) and the ones originated on the theory side (expected on the right).
 */
internal class TagFlow {
    val violations = mutableListOf<String>()
    var sawGoalOriginatedTag = false
        private set
    var sawTheoryOriginatedTag = false
        private set

    fun observe(
        lhs: Term,
        rhs: Term,
    ) {
        val lhsOrigin = lhs.tags[ORIGIN]
        val rhsOrigin = rhs.tags[ORIGIN]
        if (lhsOrigin == THEORY) violations += "theory-originated tag found in lhs: $lhs"
        if (rhsOrigin == GOAL) violations += "goal-originated tag found in rhs: $rhs"
        if (lhsOrigin == GOAL) sawGoalOriginatedTag = true
        if (rhsOrigin == THEORY) sawTheoryOriginatedTag = true
    }

    companion object {
        const val ORIGIN = "origin"
        const val GOAL = "goal"
        const val THEORY = "theory"
    }
}
