package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.NotableFunctor
import it.unibo.tuprolog.solve.solver.SolverStrategies
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration
import it.unibo.tuprolog.solve.solver.statemachine.TimeInstant
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
import kotlinx.coroutines.CoroutineScope
import kotlin.collections.List as KtList

/**
 * The state responsible of making the choice of which goal will be solved next
 *
 * @author Enrico
 */
internal class StateGoalSelection(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionStrategy, solverStrategies) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoalStruct = with(solveRequest) { signature.withArgs(arguments) }

        when {
            // current goal is already demonstrated
            with(currentGoalStruct) { this != null && solverStrategies.successCheckStrategy(this, solveRequest.context) } ->
                yield(StateEnd.True(solveRequest, executionStrategy, solverStrategies))

            else -> with(solveRequest) {
                when {
                    // current goal is split in conjunction, disjunction or implication
                    signature.name in Clause.notableFunctors ->
                        yieldAll(
                                solveSubGoals(NotableFunctor.valueOf(signature.name), arguments)
                        )

                    // current goal is some other Structure
                    else -> {
                        // TODO: 17/08/2019 move to GoalEvaluationState
                    }
                }
            }
        }
    }

    /** Internal function that solves notable functor subGoals */
    private fun solveSubGoals(notableFunctor: NotableFunctor, arguments: KtList<Term>): Sequence<State> = sequence {
        // this method relies on the fact that notable functors have arity of 2

        val subGoalsToBeSolved = goalsToSolveOrdered(arguments.asSequence(), solveRequest.context, solverStrategies::predicationChoiceStrategy)
        val argumentsSwappedByPredicationChoice = arguments.first() != subGoalsToBeSolved.first()

        when {
            // sub-goals are well-formed
            subGoalsToBeSolved.all { subGoalWellFormed(it) } -> {
                val subGoalInitStates = prepareSubGoalInitStates(subGoalsToBeSolved)

                // execute lazily the sub-goals state machines
                val subGoalStateSequence = subGoalInitStates.map { StateMachineExecutor.execute(it) }.flatten()

                // TODO: 19/08/2019 if disjunction, and first goal succeeds, second goal should be ignored (from Standard Prolog)

                val finalStates = mutableListOf<FinalState>()
                subGoalStateSequence.forEach { state ->
                    yield(state.alreadyExecuted())

                    // find in sub-goal state sequence, those states responding to actual solveRequests
                    if (state is FinalState)
                        subGoalInitStates.find { state.solveRequest.equalSignatureAndArgs(it.solveRequest) }
                                ?.also { finalStates += state }
                }

                // that's a check that always pass...
                // should not happen that there is different number of final states that respond to their init state request
                if (subGoalInitStates.count() != finalStates.count()) // TODO remove after testing if it really cannot happen
                    throw IllegalStateException("Not all solveRequests were satisfied;" +
                            " made requests: ${subGoalInitStates.map { it.solveRequest }.toList()}" +
                            " found final states response to: ${finalStates.map { it.solveRequest }.toList()}")


                // here we should have subGoal solution's final states, whichever type they are
                val (leftSubGoalFinalState, rightSubGoalFinalState) =
                        if (argumentsSwappedByPredicationChoice) finalStates.reversed() else finalStates

                when (notableFunctor.semantic(
                        finalStateToBoolean(leftSubGoalFinalState),
                        finalStateToBoolean(rightSubGoalFinalState))) {
                    true -> {
                        TODO("Extract actual substitution from solution and add it to current context of a StateEnd.True()")
                    }
                    false -> yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))
                }
            }

            // sub-goals non-well-formed
            else -> yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))
        }

    }

    /** Computes the ordered selection of goals according to provided selection strategy */
    private fun <P : Term> goalsToSolveOrdered(
            goals: Sequence<P>,
            context: ExecutionContext,
            selectionStrategy: (Sequence<P>, ExecutionContext) -> P
    ): Sequence<P> =
            mutableListOf<P>()
                    .also { selected ->
                        repeat(goals.count()) { selected += selectionStrategy(goals - selected, context) }
                    }.asSequence()

    /** Check whether the provided term is a well-formed predication */
    private fun <P : Term> subGoalWellFormed(subGoal: P): Boolean = subGoal.accept(Clause.bodyWellFormedVisitor)

    /** A method to prepare sub-goals to be executed creating the relative initial states */
    private fun <G : Term> prepareSubGoalInitStates(subGoalsToBeSolved: Sequence<G>): Sequence<State> =
            subGoalsToBeSolved
                    .map { subGoalPrepareForExecution(it) }
                    .map {
                        StateInit(
                                createSubGoalSolveRequest(it, solveRequest.context, solveRequest.executionTimeout, currentTime()),
                                executionStrategy,
                                solverStrategies
                        )
                    }

    /** A strategy to convert FinalStates to boolean responses */
    private fun finalStateToBoolean(state: FinalState): Boolean = state is SuccessFinalState

    /**
     * Prepares the provided Goal for execution
     *
     * For example, the goal `A` is transformed, after preparation for execution, as the Term: `call(A)`
     */
    private fun <P : Term> subGoalPrepareForExecution(subGoal: P): Struct =
            // exploits "Clause" implementation of prepareForExecution() to do that
            Directive.of(subGoal).prepareForExecution().args.single().castTo()


    /** A method to create Solve.Request relative to specific subGoal */
    private fun createSubGoalSolveRequest(
            subGoal: Struct,
            currentExecutionContext: ExecutionContext,
            oldExecutionTimeout: TimeDuration,
            currentTime: TimeInstant
    ): Solve.Request =
            Solve.Request(
                    Signature.fromIndicator(subGoal.indicator)!!,
                    subGoal.argsList,
                    currentExecutionContext.copy(
                            computationStartTime = currentTime,
                            parents = currentExecutionContext.parents + currentExecutionContext
                    ),
                    executionTimeoutReComputation(oldExecutionTimeout, currentExecutionContext, currentTime))

    /** Re-computes the execution timeout for sub-requests */
    private fun executionTimeoutReComputation(
            oldExecutionTimeout: TimeDuration,
            executionContext: ExecutionContext,
            currentTime: TimeInstant
    ): TimeDuration =
            when (oldExecutionTimeout) {
                Long.MAX_VALUE -> Long.MAX_VALUE
                else -> (oldExecutionTimeout - (currentTime - executionContext.computationStartTime))
                        .takeIf { it >= 0 }
                        ?: 0
            }
}
