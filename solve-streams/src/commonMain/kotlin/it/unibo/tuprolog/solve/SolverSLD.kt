package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.state.FinalState
import it.unibo.tuprolog.solve.solver.statemachine.state.StateInit
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
internal class SolverSLD(
        override val libraries: Libraries = Libraries(),
        override val flags: Map<Atom, Term> = emptyMap(),
        override val staticKB: ClauseDatabase = ClauseDatabase.empty(),
        override val dynamicKB: ClauseDatabase = ClauseDatabase.empty(),
        /** The execution strategy to be used in dispatching asynchronous computations */
        private val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Solver {

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> =
            solve(Solve.Request(
                    goal.extractSignature(),
                    goal.argsList,
                    ExecutionContextImpl(libraries, flags, staticKB, dynamicKB),
                    executionMaxDuration = maxDuration
            )).map { it.solution.calculateAnswerSubstitution() }

    /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
    internal fun solve(goalRequest: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> = StateMachineExecutor
            .execute(StateInit(goalRequest, executionStrategy))
            .filterIsInstance<FinalState>()
            .filter { it.solve.solution.query == goalRequest.query }
            .map { it.solve }

    // this should become useless when substitutions will be cleaned, while performing resolution
    /** Utility function to calculate answerSubstitution on [Solution.Yes] */
    private fun Solution.calculateAnswerSubstitution() = when (this) {
        is Solution.Yes -> copy(substitution = substitution.filter { (`var`, _) -> `var` in query.variables })
        else -> this
    }
}
