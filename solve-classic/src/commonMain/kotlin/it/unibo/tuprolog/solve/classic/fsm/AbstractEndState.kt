package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext

/**
 * Common base of the two terminal [EndState]s (`StateEnd` and `StateHalt`). By default there is no further
 * transition from here ([computeNext] throws); `StateEnd` overrides this to loop back into `StateBacktracking`
 * when [ClassicExecutionContext.hasOpenAlternatives] is true, which is what makes it a *resumable* terminal,
 * unlike `StateHalt`, the one true sink of the machine.
 */
abstract class AbstractEndState(
    override val solution: Solution,
    override val context: ClassicExecutionContext,
) : AbstractState(context),
    EndState {
    override fun computeNext(): State = throw NoSuchElementException()
}
