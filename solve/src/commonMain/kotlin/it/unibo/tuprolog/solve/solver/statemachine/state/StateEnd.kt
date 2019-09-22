package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlinx.coroutines.CoroutineScope

/**
 * Base class of states representing the computation end
 *
 * @author Enrico
 */
internal sealed class StateEnd(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractState(solveRequest, executionStrategy), FinalState {

    override fun behave(): Sequence<State> = emptySequence()

    /** The *True* state is reached when a successful computational path has ended */
    internal data class True(
            override val solveRequest: Solve.Request,
            override val executionStrategy: CoroutineScope
    ) : StateEnd(solveRequest, executionStrategy), FinalState {

        /** The answer substitution computed when reaching a success final state;
         * it contains only variable bindings for current [solveRequest] */
        val answerSubstitution: Substitution.Unifier by lazy {
            val requestedVariables = solveRequest.query?.variables ?: emptySequence()

            // reduce substitution variable chains
            with(solveRequest.context.currentSubstitution) { this.mapValues { (_, term) -> term.apply(this) } }
                    .filterKeys { it in requestedVariables }.asUnifier()
        }
    }

    /** The *False* state is reached when a failed computational path has ended */
    internal data class False(
            override val solveRequest: Solve.Request,
            override val executionStrategy: CoroutineScope
    ) : StateEnd(solveRequest, executionStrategy), FinalState

    /** The *Halt* state is reached when an [HaltException] is caught, terminating the computation */
    internal data class Halt(
            override val solveRequest: Solve.Request,
            override val executionStrategy: CoroutineScope,
            val exception: TuPrologRuntimeException
    ) : StateEnd(solveRequest, executionStrategy), FinalState


    /** Bridge method to subclasses `copy(...)` */
    fun makeCopy(solveRequest: Solve.Request = this.solveRequest, executionStrategy: CoroutineScope = this.executionStrategy): StateEnd = when (this) {
        is True -> copy(solveRequest, executionStrategy)
        is False -> copy(solveRequest, executionStrategy)
        is Halt -> copy(solveRequest, executionStrategy, this.exception)
    }
}
