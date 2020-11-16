package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.probabilistic.fsm.EndState
import it.unibo.tuprolog.solve.probabilistic.fsm.State

internal class SolutionIterator(
    state: State,
    private val onStateTransition: (State, Long) -> Unit = { _, _ -> Unit }
) : Iterator<Solution> {

    var state: State = state
        private set

    var step: Long = 0
        private set

    override fun hasNext(): Boolean {
        return state.let { it !is EndState || it.hasOpenAlternatives }
    }

    override fun next(): Solution {
        do {
            state = state.next()
            step += 1
            onStateTransition(state, step)
        } while (state !is EndState)
        return (state as EndState).solution.cleanUp()
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
