package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.primitiveimpl.Throw
import it.unibo.tuprolog.solve.solver.error.PredefinedError
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
        when (elements.any()) {
            true -> selectionStrategy(elements, context).let { selected ->
                yield(selected)
                yieldAll(
                        orderedWithStrategy(
                                elements.filterIndexed { index, _ -> index != elements.indexOf(selected) },
                                context,
                                selectionStrategy
                        )
                )
            }
            else -> yieldAll(emptySequence())
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


    /** A method to create [Solve.Request] relative to specific [newGoal], based on [receiver request][this] or optionally on [baseContext] */
    fun Solve.Request.newSolveRequest(
            newGoal: Struct,
            toAddSubstitutions: Substitution = Substitution.empty(),
            baseContext: ExecutionContext = this.context,
            currentTime: TimeInstant = currentTime(),
            isChoicePointChild: Boolean = false
    ): Solve.Request =
            Solve.Request(
                    newGoal.extractSignature(),
                    newGoal.argsList,
                    with(baseContext) {
                        copy(
                                currentSubstitution = (currentSubstitution + toAddSubstitutions) as Substitution.Unifier,
                                clauseScopedParents = sequence { yield(this@with); yieldAll(clauseScopedParents) },
                                isChoicePointChild = isChoicePointChild,
                                parentRequests = sequence { yield(this@newSolveRequest); yieldAll(parentRequests) }
                        )
                    },
                    adjustExecutionTimeout(this, currentTime)
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

    /** Utility function to create "throw" solve requests; to be used when a prolog error occurs */
    fun Solve.Request.newThrowSolveRequest(error: PredefinedError, extraData: Struct = Truth.`true`()): Solve.Request =
            this.newSolveRequest(Struct.of(Throw.signature.name, error.toThrowStruct(extraData)))

    /** Utility method to copy receiver [Solve.Request] importing [subSolve] context */
    fun Solve.Request.importingContextFrom(subSolve: Solve) = this
            .copy(context = with(this.context) {
                copy(
                        currentSubstitution = (currentSubstitution + subSolve.context.currentSubstitution) as Substitution.Unifier,
                        clauseScopedParents = sequence {
                            yieldAll(subSolve.context.clauseScopedParents)
                            yieldAll(this@with.clauseScopedParents)
                        }, // refactor this into utility method insertAtBeginning for Seqeunces
                        toCutContextsParent = subSolve.context.toCutContextsParent
                )
            })

    /** Creates a [Solve.Response] with [Solution.Yes], taking signature and arguments from receiver request
     * and using given [otherResponse] substitution and context */
    fun Solve.Request.yesResponseBy(otherResponse: Solve.Response): Solve.Response = Solve.Response(
            Solution.Yes(
                    this.signature,
                    this.arguments,
                    (this.context.currentSubstitution + otherResponse.context.currentSubstitution) as Substitution.Unifier
            ),
            otherResponse.context
    )

    /** Creates a [Solve.Response] with [Solution.No], taking signature and arguments from receiver request
     * and using given [otherResponse] context */
    fun Solve.Request.noResponseBy(otherResponse: Solve.Response): Solve.Response =
            Solve.Response(Solution.No(this.signature, this.arguments), otherResponse.context)

    /** Creates a [Solve.Response] with [Solution.Halt], taking signature and arguments from receiver request
     * and using given [otherResponse] context and exception */
    fun Solve.Request.haltResponseBy(otherResponse: Solve.Response): Solve.Response = Solve.Response(
            Solution.Halt(
                    this.signature,
                    this.arguments,
                    (otherResponse.solution as Solution.Halt).exception
            ),
            otherResponse.context
    )

    /** Creates a [Solve.Response] with [Solution] according to otherSolution response, taking signature
     * and arguments from receiver request and using given [otherResponse] substitution and context */
    fun Solve.Request.responseBy(otherResponse: Solve.Response): Solve.Response = when (otherResponse.solution) {
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
