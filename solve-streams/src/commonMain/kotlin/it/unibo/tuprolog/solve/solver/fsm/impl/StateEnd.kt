package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.libraries.Libraries
import it.unibo.tuprolog.solve.PrologFlags
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.solver.fsm.AbstractState
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.IntermediateState
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.getSideEffectManager
import it.unibo.tuprolog.theory.ClauseDatabase

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

/** Transition from this intermediate state to [StateEnd.True], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndTrue(
    substitution: Substitution.Unifier = Substitution.empty(),
    libraries: Libraries? = null,
    flags: PrologFlags? = null,
    staticKB: ClauseDatabase? = null,
    dynamicKB: ClauseDatabase? = null,
    sideEffectManager: SideEffectManager? = null
) = StateEnd.True(
    solve.replySuccess(
        substitution, libraries, flags, staticKB, dynamicKB,
        sideEffectManager ?: solve.context.getSideEffectManager()
    )
)

/** Transition from this intermediate state to [StateEnd.False], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndFalse(
    libraries: Libraries? = null,
    flags: PrologFlags? = null,
    staticKB: ClauseDatabase? = null,
    dynamicKB: ClauseDatabase? = null,
    sideEffectManager: SideEffectManager? = null
) = StateEnd.False(
    solve.replyFail(
        libraries, flags, staticKB, dynamicKB,
        sideEffectManager ?: solve.context.getSideEffectManager()
    )
)

/** Transition from this intermediate state to [StateEnd.Halt], creating a [Solve.Response] with given data */
internal fun IntermediateState.stateEndHalt(
    exception: TuPrologRuntimeException,
    libraries: Libraries? = null,
    flags: PrologFlags? = null,
    staticKB: ClauseDatabase? = null,
    dynamicKB: ClauseDatabase? = null,
    sideEffectManager: SideEffectManager? = null
) = StateEnd.Halt(
    solve.replyException(
        exception, libraries, flags, staticKB, dynamicKB,
        sideEffectManager
            ?: exception.context.getSideEffectManager()
            ?: solve.context.getSideEffectManager()
    )
)

/** Transition from this intermediate state to the correct [StateEnd] depending on provided [solution] */
internal fun IntermediateState.stateEnd(
    solution: Solution,
    libraries: Libraries? = null,
    flags: PrologFlags? = null,
    staticKB: ClauseDatabase? = null,
    dynamicKB: ClauseDatabase? = null,
    sideEffectManager: SideEffectManager? = null
): StateEnd = when (solution) {
    is Solution.Yes -> stateEndTrue(solution.substitution, libraries, flags, staticKB, dynamicKB, sideEffectManager)
    is Solution.No -> stateEndFalse(libraries, flags, staticKB, dynamicKB, sideEffectManager)
    is Solution.Halt -> stateEndHalt(solution.exception, libraries, flags, staticKB, dynamicKB, sideEffectManager)
}

/** Transition from this intermediate state to a [StateEnd] containing provided [response] data */
internal fun IntermediateState.stateEnd(response: Solve.Response) =
    with(response) { stateEnd(solution, libraries, flags, staticKB, dynamicKB, sideEffectManager) }
