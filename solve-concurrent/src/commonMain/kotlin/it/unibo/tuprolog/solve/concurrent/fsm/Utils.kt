@file:JvmName("Utils")

package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
import kotlin.jvm.JvmName

/**
 * Casts every element of this [Sequence] to a [Rule].
 * @throws IllegalArgumentException if any element is not [Clause.isRule].
 */
fun Sequence<Clause>.ensureRules(): Sequence<Rule> =
    map {
        require(it.isRule)
        it.castToRule()
    }

/** Flattens this term into a [Sequence] of its top-level conjuncts, recursively unfolding nested tuples (`,/2`). */
fun Term.unfoldGoals(): Sequence<Term> =
    when {
        this.isTuple -> castToTuple().toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

/**
 * Converts this term into the [Cursor] of goals [ConcurrentExecutionContext.goals] is built from: [unfoldGoals]
 * flattens conjunctions, and every variable goal is wrapped into `call(X)` so that [StatePrimitiveSelection] always
 * sees a callable [Struct].
 */
fun Term.toGoals(): Cursor<out Term> =
    unfoldGoals()
        .map {
            when {
                it.isVar -> Struct.of("call", it)
                else -> it
            }
        }.cursor()

/**
 * Creates a new [ConcurrentExecutionContext] descending into [ConcurrentExecutionContext.currentGoal] as a
 * sub-goal: the new context's [ConcurrentExecutionContext.goals] become just that goal, its
 * [ConcurrentExecutionContext.parent] is this context, and its [ConcurrentExecutionContext.depth]/
 * [ConcurrentExecutionContext.step] are incremented by one. Used by [StatePrimitiveSelection]/[StateRuleSelection]
 * when a goal is not last-call-optimizable, so it gets its own stack frame to backtrack into.
 */
fun ConcurrentExecutionContext.createChild(inferProcedureFromGoals: Boolean = true): ConcurrentExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        parent = this,
        depth = depth + 1,
        step = step + 1,
    )
}

/**
 * Same as [createChild], but keeps [ConcurrentExecutionContext.parent] unchanged instead of pointing it at this
 * context -- used for the last-call-optimized case (see [ConcurrentExecutionContext] and `LastCallOptimization`),
 * reusing the current stack frame rather than growing the chain of [ConcurrentExecutionContext.parent]s.
 */
fun ConcurrentExecutionContext.replaceWithChild(inferProcedureFromGoals: Boolean = true): ConcurrentExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        depth = depth + 1,
        step = step + 1,
    )
}

/** Same as [createChild], additionally attaching [rule] as the clause [StateRuleExecution] will unify against. */
fun ConcurrentExecutionContext.createChildAppendingRules(
    rule: Rule,
    inferProcedureFromGoals: Boolean = true,
): ConcurrentExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.copy(rule = rule)
}

/** Same as [replaceWithChild], additionally attaching [rule] as the clause [StateRuleExecution] will unify against. */
fun ConcurrentExecutionContext.replaceWithChildAppendingRules(
    rule: Rule,
    inferProcedureFromGoals: Boolean = true,
): ConcurrentExecutionContext {
    val tempExecutionContext = replaceWithChild(inferProcedureFromGoals)
    return tempExecutionContext.copy(rule = rule)
}

/** Builds the [Solve.Request] a [it.unibo.tuprolog.solve.primitive.Primitive] matching [signature] is invoked with, for [goal]. */
fun ConcurrentExecutionContext.toRequest(
    goal: Struct,
    signature: Signature,
    startTime: TimeInstant,
) = Solve.Request(signature, goal.args, this, startTime)
