/**
 * Utilities to implement Solver resolution behaviour
 *
 * @author Enrico
 */
@file:JvmName("SolverUtils")

package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.*
import kotlin.jvm.JvmName

/** Check whether the receiver term is a well-formed predication */
fun Term.isWellFormed(): Boolean = accept(Clause.bodyWellFormedVisitor)

/**
 * Prepares the receiver Goal for execution
 *
 * For example, the goal `A` is transformed, after preparation for execution, as the Term: `call(A)`
 */
fun Term.prepareForExecutionAsGoal(): Struct =
    // exploits "Clause" implementation of prepareForExecution() to do that
    Directive.of(this).prepareForExecution().args.single().castTo()

/** Computes the ordered selection of elements, lazily, according to provided selection strategy */
fun <E> Sequence<E>.orderWithStrategy(
    context: ExecutionContext,
    selectionStrategy: (Sequence<E>, ExecutionContext) -> E
): Sequence<E> =
    when (any()) {
        true -> sequence {
            selectionStrategy(this@orderWithStrategy, context).let { selected ->
                yield(selected)
                yieldAll(
                    filterIndexed { index, _ -> index != indexOf(selected) }
                        .orderWithStrategy(context, selectionStrategy)
                )
            }
        }
        else -> emptySequence()
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

/**
 * A method to create a new [Solve.Request] physically chained to receiver request.
 *
 * @param newGoal The new solve request goal
 * @param toAddSubstitutions The added substitutions to new request context, if any
 * @param baseSideEffectManager The base side effect manager to be injected in the new solve request, if different from physical parent request one
 * @param currentTime The current time instant on new request creation, if different from method invocation time instant
 * @param isChoicePointChild Whether this new request is considered a child of a Choice Point
 */
internal fun Solve.Request<StreamsExecutionContext>.newSolveRequest(
    newGoal: Struct,
    toAddSubstitutions: Substitution = Substitution.empty(),
    toPropagateContextData: ExecutionContext = context,
    baseSideEffectManager: SideEffectManagerImpl = context.sideEffectManager,
    currentTime: TimeInstant = currentTimeInstant(),
    isChoicePointChild: Boolean = false
): Solve.Request<StreamsExecutionContext> = copy(
    newGoal.extractSignature(),
    newGoal.argsList,
    context.copy(
        libraries = toPropagateContextData.libraries,
        flags = toPropagateContextData.flags,
        staticKb = toPropagateContextData.staticKb,
        dynamicKb = toPropagateContextData.dynamicKb,
        inputChannels = toPropagateContextData.inputChannels,
        outputChannels = toPropagateContextData.outputChannels,
        substitution = (context.substitution + toAddSubstitutions) as Substitution.Unifier,
        sideEffectManager = baseSideEffectManager.creatingNewRequest(context, isChoicePointChild, this)
    ),
    requestIssuingInstant = currentTime,
    executionMaxDuration = adjustExecutionMaxDuration(this, currentTime)
)

/** Re-computes the execution timeout, leaving it `TimeDuration.MAX_VALUE` if it was it, or decreasing it with elapsed time */
private fun adjustExecutionMaxDuration(
    oldSolveRequest: Solve.Request<StreamsExecutionContext>,
    currentTime: TimeInstant
): TimeDuration = when (oldSolveRequest.executionMaxDuration) {
    TimeDuration.MAX_VALUE -> TimeDuration.MAX_VALUE
    else -> with(oldSolveRequest) { executionMaxDuration - (currentTime - requestIssuingInstant) }
        .takeIf { it >= 0 }
        ?: 0
}

/** Responds to this solve request forwarding the provided [otherResponse] data */
fun Solve.Request<ExecutionContext>.replyWith(otherResponse: Solve.Response): Solve.Response =
    with(otherResponse) {
        replyWith(
            solution, libraries, flags, staticKb, dynamicKb, sideEffectManager
                ?: this@replyWith.context.getSideEffectManager()
        )
    }
