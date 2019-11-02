package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

/**
 * Base class of states representing the computation end
 *
 * @param solve The [Solve.Response] characterizing this Final State
 *
 * @author Enrico
 */
internal sealed class StateEnd(override val solve: Solve.Response) : AbstractState(solve), FinalState {

    override fun behave(): Sequence<State> = emptySequence()

    /** The *True* state is reached when a successful computational path has ended */
    internal data class True(override val solve: Solve.Response) : StateEnd(solve), FinalState {
        init {
            require(solve.solution is Solution.Yes) { "True end state can be created only with Solution.Yes. Current: `${solve.solution}`" }
        }
    }

    /** The *False* state is reached when a failed computational path has ended */
    internal data class False(override val solve: Solve.Response) : StateEnd(solve), FinalState {
        init {
            require(solve.solution is Solution.No) { "False end state can be created only with Solution.No. Current: `${solve.solution}`" }
        }
    }

    /** The *Halt* state is reached when an [HaltException] is caught, terminating the computation */
    internal data class Halt(override val solve: Solve.Response) : StateEnd(solve), FinalState {
        init {
            require(solve.solution is Solution.Halt) { "Halt end state can be created only with Solution.Halt. Current: `${solve.solution}`" }
        }

        /** Shorthand property to access `solve.solution.exception` */
        val exception: TuPrologRuntimeException by lazy { (solve.solution as Solution.Halt).exception }
    }
}
