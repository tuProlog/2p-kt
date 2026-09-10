/**
 * Utilities to implement Solver resolution behaviour
 *
 * @author Enrico
 */
@file:JvmName("SolverUtils")

package it.unibo.tuprolog.solve.streams.solver

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import kotlin.jvm.JvmName
import kotlin.collections.List as KtList

/** Checks whether the receiver term is a well-formed predication, i.e. an [it.unibo.tuprolog.core.Atom] or [Struct] (see [Clause.bodyWellFormedVisitor]). */
fun Term.isWellFormed(): Boolean = accept(Clause.bodyWellFormedVisitor)

/**
 * Prepares the receiver goal for execution, wrapping it into a `call/1` when needed so it goes through the
 * ISO callability check before being solved.
 *
 * For example, the goal `A` is transformed, after preparation for execution, into the [Struct] `call(A)`.
 */
fun Term.prepareForExecutionAsGoal(): Struct =
    // exploits "Clause" implementation of prepareForExecution() to do that
    Directive
        .of(this)
        .prepareForExecution()
        .args
        .single()
        .castTo()

/**
 * Lazily reorders this sequence of candidates according to [selectionStrategy]: repeatedly asks
 * [selectionStrategy] to pick one element out of what remains, yields it, then recurses on the rest -- so the
 * first pick drives the order of the whole resulting sequence, one element at a time, without ever fully
 * consuming the receiver upfront.
 *
 * This is how [it.unibo.tuprolog.solve.streams.SolverStrategies]'s `predicationChoiceStrategy`/
 * `clauseChoiceStrategy` are applied to a sequence of candidate predications/clauses during resolution.
 */
fun <E> Sequence<E>.orderWithStrategy(
    context: ExecutionContext,
    selectionStrategy: (Sequence<E>, ExecutionContext) -> E,
): Sequence<E> =
    when (any()) {
        true ->
            sequence {
                selectionStrategy(this@orderWithStrategy, context).let { selected ->
                    yield(selected)
                    yieldAll(
                        filterIndexed { index, _ -> index != indexOf(selected) }
                            .orderWithStrategy(context, selectionStrategy),
                    )
                }
            }
        else -> emptySequence()
    }

/** Lazily checks whether [elements] holds more than one element, consuming at most two of them. */
fun moreThanOne(elements: Sequence<*>): Boolean =
    with(elements.iterator()) {
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
 * @param toPropagateContextData The context data to be propagated to the new request
 * @param toAddSubstitutions The added substitutions to new request context, if any
 * @param baseSideEffectManager The base side effect manager to be injected in the new solve request, if different from physical parent request one
 * @param requestIssuingInstant The current time instant on new request creation, if different from method invocation time instant
 * @param isChoicePointChild Whether this new request is considered a child of a Choice Point
 */
internal fun Solve.Request<StreamsExecutionContext>.newSolveRequest(
    newGoal: Struct,
    toAddSubstitutions: Substitution = Substitution.empty(),
    toPropagateContextData: ExecutionContext = context,
    baseSideEffectManager: SideEffectManagerImpl = context.sideEffectManager,
    requestIssuingInstant: TimeInstant = this.startTime,
    isChoicePointChild: Boolean = false,
): Solve.Request<StreamsExecutionContext> =
    copy(
        newGoal.extractSignature(),
        newGoal.args,
        context.copy(
            libraries = toPropagateContextData.libraries,
            flags = toPropagateContextData.flags,
            staticKb = toPropagateContextData.staticKb,
            dynamicKb = toPropagateContextData.dynamicKb,
            inputChannels = toPropagateContextData.inputChannels,
            outputChannels = toPropagateContextData.outputChannels,
            substitution = (context.substitution + toAddSubstitutions) as Substitution.Unifier,
            sideEffectManager = baseSideEffectManager.creatingNewRequest(context, isChoicePointChild, this),
        ),
        startTime = requestIssuingInstant,
    )

/**
 * Builds a [Solve.Response] to this request that forwards [otherResponse]'s [Solve.Response.solution] and
 * [Solve.Response.sideEffects], falling back to this request's own [ExecutionContext]'s side-effect manager
 * when [otherResponse] carries none.
 */
fun Solve.Request<ExecutionContext>.replyWith(otherResponse: Solve.Response): Solve.Response =
    with(otherResponse) {
        replyWith(
            solution,
            sideEffectManager ?: this@replyWith.context.getSideEffectManager(),
            *sideEffects.toTypedArray(),
        )
    }

/**
 * Appends [toAddSideEffects] to this list of side effects, first dropping from its front as many elements as
 * this list has that also occur (by reference identity, not equality) somewhere in [toAddSideEffects] -- used
 * when merging a child request's accumulated side effects back into its parent's, to avoid recording the same
 * [SideEffect] instance twice when [toAddSideEffects] already carries this list's elements as a prefix.
 */
fun KtList<SideEffect>.addWithNoDuplicates(toAddSideEffects: KtList<SideEffect>): KtList<SideEffect> {
    var duplicatedCount = 0
    forEach { sideEffect ->
        if (toAddSideEffects.find { toAddSideEffect -> sideEffect === toAddSideEffect } !== null) {
            duplicatedCount++
        }
    }
    return this + toAddSideEffects.drop(duplicatedCount)
}
