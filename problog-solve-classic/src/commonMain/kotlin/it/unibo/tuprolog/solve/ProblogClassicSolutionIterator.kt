package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.problogclassic.fsm.EndState
import it.unibo.tuprolog.solve.problogclassic.fsm.State
import it.unibo.tuprolog.solve.problogclassic.knowledge.ProblogSolutionTerm
import it.unibo.tuprolog.struct.BinaryDecisionDiagram

internal class ProblogClassicSolutionIterator(
    state: State,
    private val onStateTransition: (State, Long) -> Unit = { _, _ -> }
) : Iterator<Pair<Solution, BinaryDecisionDiagram<ProblogSolutionTerm>>> {

    var state: State = state
        private set

    var step: Long = 0
        private set

    override fun hasNext(): Boolean {
        return state.let { it !is EndState || it.hasOpenAlternatives }
    }

    override fun next(): Pair<Solution, BinaryDecisionDiagram<ProblogSolutionTerm>> {
        do {
            state = state.next()
            step += 1
            onStateTransition(state, step)
        } while (state !is EndState)
        return Pair((state as EndState).solution.cleanUp(), state.context.bdd)
    }

    private fun Solution.cleanUp(): Solution =
        when (this) {
            is Solution.Yes -> cleanUp()
            else -> this
        }

    private fun Solution.Yes.cleanUp(): Solution.Yes {
        return copy(substitution = substitution.cleanUp(query.variables.toSet()))
    }

    private fun Substitution.Unifier.cleanUp(toRetain: Set<Var>): Substitution.Unifier {
        return filter { _, term -> (term !is Var) || (term in toRetain) }
    }
}
