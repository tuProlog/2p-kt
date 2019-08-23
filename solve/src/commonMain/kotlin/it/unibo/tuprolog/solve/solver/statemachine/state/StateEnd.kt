package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.exception.HaltException
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
    ) : StateEnd(solveRequest, executionStrategy), SuccessFinalState {

        override val answerSubstitution: Substitution.Unifier =
                solveRequest.context.actualSubstitution.filterKeys { `var` ->
                    solveRequest.arguments.any { it.accept(containsVisitor(`var`)) }
                }.asUnifier()

        /** A visitor to check if a [containedTerm] is present inside some other Term */
        private fun containsVisitor(containedTerm: Term): TermVisitor<Boolean> =
                object : TermVisitor<Boolean> {
                    override fun defaultValue(term: Term) = false
                    override fun visit(term: Term) = containedTerm == term ||
                            (term is Struct && term.argsSequence.any { it.accept(this) })
                }
    }

    /** The *False* state is reached when a failed computational path has ended */
    internal data class False(
            override val solveRequest: Solve.Request,
            override val executionStrategy: CoroutineScope
    ) : StateEnd(solveRequest, executionStrategy), FailFinalState

    /** The *Halt* state is reached when an [HaltException] is catch, terminating the computation */
    internal data class Halt(
            override val solveRequest: Solve.Request,
            override val executionStrategy: CoroutineScope
    ) : StateEnd(solveRequest, executionStrategy), FailFinalState

    /** The *Timeout* state is reached when the given request timeout is reached before shifting to other [StateEnd], terminating computation */
    internal data class Timeout(
            override val solveRequest: Solve.Request,
            override val executionStrategy: CoroutineScope
    ) : StateEnd(solveRequest, executionStrategy), FailFinalState
}
