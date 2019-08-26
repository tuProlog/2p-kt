package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration
import it.unibo.tuprolog.solve.solver.statemachine.TimeInstant
import it.unibo.tuprolog.solve.solver.statemachine.currentTime

/**
 * Utilities object for implementing resolution behaviour
 *
 * @author Enrico
 */
internal object SolverUtils {

    /** Computes the ordered selection of elements, lazily, according to provided selection strategy */
    fun <E> orderedWithStrategy(
            elements: Sequence<E>,
            context: ExecutionContext,
            selectionStrategy: (Sequence<E>, ExecutionContext) -> E
    ): Sequence<E> = sequence {
        val alreadySelected = mutableListOf<E>()

        elements.forEach { _ ->
            (elements - alreadySelected).takeIf { it.any() }?.also { remaining ->
                alreadySelected += selectionStrategy(remaining, context)
                        .also { yield(it) }
            }
        }
    }

    /** Check whether the provided term is a well-formed predication */
    fun isWellFormed(goal: Term): Boolean = goal.accept(Clause.bodyWellFormedVisitor)

    /**
     * Prepares the provided Goal for execution
     *
     * For example, the goal `A` is transformed, after preparation for execution, as the Term: `call(A)`
     */
    fun prepareForExecution(goal: Term): Struct =
            // exploits "Clause" implementation of prepareForExecution() to do that
            Directive.of(goal).prepareForExecution().args.single().castTo()


    /** A method to create [Solve.Request] relative to specific [newGoal], based on [receiver request][this] */
    fun Solve.Request.newSolveRequest(
            newGoal: Struct,
            actualTime: TimeInstant = currentTime(),
            toAddSubstitutions: Substitution = Substitution.empty()
    ): Solve.Request =
            Solve.Request(
                    Signature.fromIndicator(newGoal.indicator)!!,
                    newGoal.argsList,
                    with(this.context) {
                        copy(
                                actualSubstitution = Substitution.of(actualSubstitution, toAddSubstitutions),
                                parents = parents + this
                        )
                    },
                    adjustExecutionTimeout(this, actualTime)
            )

    /** Re-computes the execution timeout, leaving it [Long.MAX_VALUE] if it was it, or decreasing it with elapsed time */
    private fun adjustExecutionTimeout(
            oldSolveRequest: Solve.Request,
            currentTime: TimeInstant
    ): TimeDuration =
            when (oldSolveRequest.executionTimeout) {
                Long.MAX_VALUE -> Long.MAX_VALUE
                else -> with(oldSolveRequest) { executionTimeout - (currentTime - context.computationStartTime) }
                        .takeIf { it >= 0 }
                        ?: 0
            }

    /** Creates a [Solve.Response] with [Solution.Yes], taking signature and arguments from receiver request
     * and using given [otherResponse] substitution and context */
    fun Solve.Request.yesResponseBy(otherResponse: Solve.Response): Solve.Response =
            otherResponse.solution.substitution.let {
                require(it is Substitution.Unifier) {
                    "An affirmative response requires a non-failed Substitution! Actually passed `$it`"
                }.let { _ ->
                    Solve.Response(Solution.Yes(this.signature, this.arguments, it), otherResponse.context)
                }
            }

    /** Creates a [Solve.Response] with [Solution.No], taking signature and arguments from receiver request
     * and using given [otherResponse] context */
    fun Solve.Request.noResponseBy(otherResponse: Solve.Response): Solve.Response =
            Solve.Response(Solution.No(this.signature, this.arguments), otherResponse.context)
}
