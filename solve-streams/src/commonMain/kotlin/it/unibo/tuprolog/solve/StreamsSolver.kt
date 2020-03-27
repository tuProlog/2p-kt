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
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree in a purely functional way
 *
 * @author Enrico
 */
internal class StreamsSolver
    constructor(
        libraries: Libraries = Libraries(),
        flags: PrologFlags = emptyMap(),
        staticKB: ClauseDatabase = ClauseDatabase.empty(),
        dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        inputChannels: PrologInputChannels<*> = ExecutionContextAware.defaultInputChannels(),
        outputChannels: PrologOutputChannels<*> = ExecutionContextAware.defaultOutputChannels()
    ) : Solver {

    private var executionContext = ExecutionContextImpl(
        libraries,
        flags,
        staticKB,
        dynamicKB,
        inputChannels,
        outputChannels
    )

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        executionContext = ExecutionContextImpl(
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            inputChannels = inputChannels,
            outputChannels = outputChannels
        )

        return solve(
            Solve.Request(
                goal.extractSignature(),
                goal.argsList,
                executionContext,
                executionMaxDuration = maxDuration
            )
        ).map {
            // TODO update executionContext
            // executionContext = TODO
            it.solve.solution.cleanUp()
        }
    }

    override val libraries: Libraries
        get() = executionContext.libraries

    override val flags: PrologFlags
        get() = executionContext.flags

    override val staticKb: ClauseDatabase
        get() = executionContext.staticKb

    override val dynamicKb: ClauseDatabase
        get() = executionContext.dynamicKb

    override val inputChannels: PrologInputChannels<*>
        get() = executionContext.inputChannels

    override val outputChannels: PrologOutputChannels<*>
        get() = executionContext.outputChannels


    internal companion object {

        /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
        internal fun solve(goalRequest: Solve.Request<ExecutionContextImpl>): Sequence<FinalState> =
            StateMachineExecutor.execute(StateInit(goalRequest))
                .filterIsInstance<FinalState>()
                .filter { it.solve.solution.query == goalRequest.query }

        /** Utility function to clean up unassigned variables from final result */
        private fun Solution.cleanUp(): Solution = when (this) {
            is Solution.Yes -> copy(substitution = substitution.filter { _, term -> term !is Var })
            else -> this
        }
    }
}
