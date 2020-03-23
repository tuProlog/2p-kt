package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.primitive.extractSignature
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.fsm.impl.StateInit
import it.unibo.tuprolog.theory.ClauseDatabase

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
data class StreamsSolver(
    override val libraries: Libraries = Libraries(),
    override val flags: PrologFlags = emptyMap(),
    override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
    override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
    override val inputChannels: PrologInputChannels<String> = ExecutionContextAware.defaultInputChannels(),
    override val outputChannels: PrologOutputChannels<String> = ExecutionContextAware.defaultOutputChannels()
) : Solver {

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> =
        solve(
            Solve.Request(
                goal.extractSignature(),
                goal.argsList,
                ExecutionContextImpl(libraries, flags, staticKB, dynamicKB, inputChannels, outputChannels),
                executionMaxDuration = maxDuration
            )
        ).map { it.solution.cleanUp() }


    internal companion object {

        /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
        internal fun solve(goalRequest: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
            StateMachineExecutor.execute(StateInit(goalRequest))
                .filterIsInstance<FinalState>()
                .filter { it.solve.solution.query == goalRequest.query }
                .map { it.solve }

        /** Utility function to clean up unassigned variables from final result */
        private fun Solution.cleanUp(): Solution = when (this) {
            is Solution.Yes -> copy(substitution = substitution.filter { _, term -> term !is Var })
            else -> this
        }
    }
}
