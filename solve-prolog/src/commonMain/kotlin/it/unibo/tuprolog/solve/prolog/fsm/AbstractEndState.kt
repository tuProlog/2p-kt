package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext

/**
 * Common base of the two terminal [EndState]s (`StateEnd` and `StateHalt`). By default there is no further
 * transition from here ([computeNext] throws); `StateEnd` overrides this to loop back into `StateBacktracking`
 * when [PrologExecutionContext.hasOpenAlternatives] is true, which is what makes it a *resumable* terminal,
 * unlike `StateHalt`, the one true sink of the machine.
 */
abstract class AbstractEndState(
    override val solution: Solution,
    override val context: PrologExecutionContext,
) : AbstractState(context),
    EndState {
    override fun computeNext(): State = throw NoSuchElementException()
}
