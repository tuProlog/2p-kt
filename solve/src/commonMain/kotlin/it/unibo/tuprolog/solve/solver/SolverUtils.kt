package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.primitiveimpl.Throw
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
    ): Sequence<E> =
            when (elements.any()) {
                true -> sequence {
                    selectionStrategy(elements, context).let { selected ->
                        yield(selected)
                        yieldAll(
                                orderedWithStrategy(
                                        elements.filterIndexed { index, _ -> index != elements.indexOf(selected) },
                                        context,
                                        selectionStrategy
                                )
                        )
                    }
                }
                else -> emptySequence()
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

    /**
     * Reduces substitution variable chains and retains only [toRetainVariables]
     *
     * If [toRetainVariables] is null, all reduced substitution will be returned
     */
    fun reduceAndFilterSubstitution(substitution: Substitution, toRetainVariables: Sequence<Var>? = null) =
            with(substitution) { this.mapValues { (_, term) -> term.apply(this) } }
                    .let { reducedSubstitution ->
                        toRetainVariables
                                ?.run { reducedSubstitution.filterKeys { it in toRetainVariables } }
                                ?: reducedSubstitution
                    }.asUnifier()

    /** A method to create [Solve.Request] relative to specific [newGoal], based on [receiver request][this] or optionally on [baseContext] */
    fun Solve.Request<ExecutionContextImpl>.newSolveRequest(
            newGoal: Struct,
            toAddSubstitutions: Substitution = Substitution.empty(),
            baseContext: ExecutionContextImpl = this.context,
            currentTime: TimeInstant = currentTime(),
            isChoicePointChild: Boolean = false,
            logicalParentRequest: Solve.Request<ExecutionContextImpl> = this
    ): Solve.Request<ExecutionContextImpl> =
            Solve.Request<ExecutionContextImpl>(
                    newGoal.extractSignature(),
                    newGoal.argsList,
                    this.initialSolverQuery,
                    with(baseContext) {
                        copy(
                                substitution = (substitution + toAddSubstitutions) as Substitution.Unifier,
                                clauseScopedParents = sequence { yield(this@with); yieldAll(clauseScopedParents) },
                                isChoicePointChild = isChoicePointChild,
                                logicalParentRequests = sequence {
                                    when (logicalParentRequest) {
                                        in logicalParentRequests -> yieldAll(logicalParentRequests.dropWhile { it != logicalParentRequest })
                                        else -> {
                                            yield(logicalParentRequest)
                                            yieldAll(logicalParentRequests)
                                        }
                                    }
                                }
                        )
                    },
                    this.requestIssuingInstant,
                    adjustExecutionMaxDuration(this, currentTime)
            )

    /** Re-computes the execution timeout, leaving it [Long.MAX_VALUE] if it was it, or decreasing it with elapsed time */
    private fun adjustExecutionMaxDuration(
            oldSolveRequest: Solve.Request<ExecutionContextImpl>,
            currentTime: TimeInstant
    ): TimeDuration =
            when (oldSolveRequest.executionMaxDuration) {
                Long.MAX_VALUE -> Long.MAX_VALUE
                else -> with(oldSolveRequest) { executionMaxDuration - (currentTime - requestIssuingInstant) }
                        .takeIf { it >= 0 }
                        ?: 0
            }

    /** Utility function to create "throw" solve requests; to be used when a prolog error occurs */
    fun Solve.Request<ExecutionContextImpl>.newThrowSolveRequest(error: PrologError): Solve.Request<ExecutionContextImpl> =
            this.newSolveRequest(Struct.of(Throw.functor, error.errorStruct), baseContext = error.context)

    /** Utility method to copy receiver [Solve.Request] importing [subSolve] context */
    fun Solve.Request<ExecutionContextImpl>.importingContextFrom(subSolve: Solve.Request<ExecutionContextImpl>) = this
            .copy(context = with(this.context) {
                copy(
                        substitution = (substitution + subSolve.context.substitution) as Substitution.Unifier,
                        clauseScopedParents = sequence {
                            yieldAll(subSolve.context.clauseScopedParents)
                            yieldAll(this@with.clauseScopedParents)
                        }, // refactor this into utility method insertAtBeginning for Seqeunces
                        toCutContextsParent = subSolve.context.toCutContextsParent,
                        throwRelatedToCutContextsParent = subSolve.context.throwRelatedToCutContextsParent
                )
            })

    /** Creates a [Solve.Response] with [Solution.Yes], taking signature and arguments from receiver request
     * and using given [otherResponse] substitution and context */
    fun Solve.Request<ExecutionContextImpl>.yesResponseBy(otherResponse: Solve.Response): Solve.Response = Solve.Response(
            Solution.Yes(
                    this.signature,
                    this.arguments,
                    (this.context.substitution + otherResponse.solution.substitution) as Substitution.Unifier
            ),
            context = otherResponse.context
    )

    /** Creates a [Solve.Response] with [Solution.No], taking signature and arguments from receiver request
     * and using given [otherResponse] context */
    fun Solve.Request<ExecutionContextImpl>.noResponseBy(otherResponse: Solve.Response): Solve.Response =
            Solve.Response(Solution.No(this.signature, this.arguments), context = otherResponse.context!!)

    /** Creates a [Solve.Response] with [Solution.Halt], taking signature and arguments from receiver request
     * and using given [otherResponse] context and exception */
    fun Solve.Request<ExecutionContextImpl>.haltResponseBy(otherResponse: Solve.Response): Solve.Response = Solve.Response(
            Solution.Halt(
                    this.signature,
                    this.arguments,
                    (otherResponse.solution as Solution.Halt).exception
            ),
            context = otherResponse.context!!
    )

    /** Creates a [Solve.Response] with [Solution] according to otherSolution response, taking signature
     * and arguments from receiver request and using given [otherResponse] substitution and context */
    fun Solve.Request<ExecutionContextImpl>.responseBy(otherResponse: Solve.Response): Solve.Response = when (otherResponse.solution) {
        is Solution.Yes -> this.yesResponseBy(otherResponse)
        is Solution.No -> this.noResponseBy(otherResponse)
        is Solution.Halt -> this.haltResponseBy(otherResponse)
    }

    /** Checks if this sequence of elements holds more than one element, lazily */
    fun moreThanOne(elements: Sequence<*>): Boolean = with(elements.iterator()) {
        when {
            !hasNext() -> false // no element
            else -> {
                next()
                hasNext() // more elements, if first element has a next element
            }
        }
    }
}
