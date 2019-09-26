package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Base class of states representing the computation end
 *
 * @author Enrico
 */
internal sealed class StateEnd(
        /** The [Solve.Response] characterizing this Final State */
        override val solve: Solve.Response,

        // execution strategy in StateEnd is not so important, because no heavy computation is to be executed
        override val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : AbstractState(solve, executionStrategy), FinalState {

    override fun behave(): Sequence<State> = emptySequence()

    /** The *True* state is reached when a successful computational path has ended */
    internal data class True(override val solve: Solve.Response) : StateEnd(solve), FinalState {
// TODO: 26/09/2019 check that true end state can be created with only a yes response ; and test

    }

    /** The *False* state is reached when a failed computational path has ended */
    internal data class False(override val solve: Solve.Response) : StateEnd(solve), FinalState {
// TODO: 26/09/2019 check that false end state can be created with only a no response        ; and test
    }

    /** The *Halt* state is reached when an [HaltException] is caught, terminating the computation */
    internal data class Halt(override val solve: Solve.Response) : StateEnd(solve), FinalState {
        // TODO: 26/09/2019 check that halt end state can be created with only a halt response; and test

        /** Shorthand property to access `solve.solution.exception` */
        val exception: TuPrologRuntimeException by lazy { (solve.solution as Solution.Halt).exception }
    }

    /** Bridge method to subclasses `copy(...)` */
    fun makeCopy(solveResponse: Solve.Response = this.solve): StateEnd = when (this) {
        is True -> copy(solveResponse)
        is False -> copy(solveResponse)
        is Halt -> copy(solveResponse)
    }
}
