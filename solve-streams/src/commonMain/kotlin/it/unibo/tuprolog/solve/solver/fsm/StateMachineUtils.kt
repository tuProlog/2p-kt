package it.unibo.tuprolog.solve.solver.fsm

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.solver.fsm.state.IntermediateState
import it.unibo.tuprolog.solve.solver.fsm.state.State
import it.unibo.tuprolog.solve.solver.fsm.state.StateEnd
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Utils object to help implementing [State]s' behaviour
 *
 * @author Enrico
 */
internal object StateMachineUtils {

//    /** Converts receiver solution to corresponding [FinalState] */
//    fun Solution.toFinalState(solveResponse: Solve.Response): FinalState = when (this) {
//        is Solution.Yes -> StateEnd.True(solveResponse)
//        is Solution.No -> StateEnd.False(solveResponse)
//        is Solution.Halt -> StateEnd.Halt(solveResponse)
//    }

}

// TODO: 26/09/2019 documentation and testing for those methods

internal fun IntermediateState.stateEnd(response: Solve.Response) =
        stateEnd(response.solution, response.libraries, response.flags, response.staticKB, response.dynamicKB, response.sideEffectManager)

internal fun IntermediateState.stateEnd(
        solution: Solution,
        libraries: Libraries? = null,
        flags: Map<Atom, Term>? = null,
        staticKB: ClauseDatabase? = null,
        dynamicKB: ClauseDatabase? = null,
        sideEffectManager: SideEffectManager? = null
): StateEnd = when (solution) {
    is Solution.Yes -> stateEndTrue(solution.substitution, libraries, flags, staticKB, dynamicKB, sideEffectManager)
    is Solution.No -> stateEndFalse(libraries, flags, staticKB, dynamicKB, sideEffectManager)
    is Solution.Halt -> stateEndHalt(solution.exception, libraries, flags, staticKB, dynamicKB, sideEffectManager)
}

internal fun IntermediateState.stateEndTrue(
        substitution: Substitution.Unifier = Substitution.empty(),
        libraries: Libraries? = null,
        flags: Map<Atom, Term>? = null,
        staticKB: ClauseDatabase? = null,
        dynamicKB: ClauseDatabase? = null,
        sideEffectManager: SideEffectManager? = null
) = StateEnd.True(solve.replySuccess(substitution, libraries, flags, staticKB, dynamicKB, sideEffectManager))

internal fun IntermediateState.stateEndFalse(
        libraries: Libraries? = null,
        flags: Map<Atom, Term>? = null,
        staticKB: ClauseDatabase? = null,
        dynamicKB: ClauseDatabase? = null,
        sideEffectManager: SideEffectManager? = null
) = StateEnd.False(solve.replyFail(libraries, flags, staticKB, dynamicKB, sideEffectManager))

internal fun IntermediateState.stateEndHalt(
        exception: TuPrologRuntimeException,
        libraries: Libraries? = null,
        flags: Map<Atom, Term>? = null,
        staticKB: ClauseDatabase? = null,
        dynamicKB: ClauseDatabase? = null,
        sideEffectManager: SideEffectManager? = null
) = StateEnd.Halt(solve.replyException(exception, libraries, flags, staticKB, dynamicKB, sideEffectManager))
