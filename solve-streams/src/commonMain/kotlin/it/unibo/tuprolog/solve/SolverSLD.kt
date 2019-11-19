package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.fsm.impl.StateInit
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
data class SolverSLD(
    override val libraries: Libraries = Libraries(),
    override val flags: PrologFlags = emptyMap(),
    override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
    override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    /** The execution strategy to be used in dispatching asynchronous computations */
    private val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Solver {

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> =
        solve(
            Solve.Request(
                goal.extractSignature(),
                goal.argsList,
                ExecutionContextImpl(libraries, flags, staticKB, dynamicKB),
                executionMaxDuration = maxDuration
            ),
            executionStrategy
        ).map { it.solution }


    internal companion object {

        /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
        internal fun solve(
            goalRequest: Solve.Request<ExecutionContextImpl>,
            executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
        ): Sequence<Solve.Response> =
            StateMachineExecutor.execute(StateInit(goalRequest, executionStrategy))
                .filterIsInstance<FinalState>()
                .filter { it.solve.solution.query == goalRequest.query }
                .map { it.solve }
    }
}
