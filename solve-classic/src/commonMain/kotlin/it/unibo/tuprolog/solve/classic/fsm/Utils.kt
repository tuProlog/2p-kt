@file:JvmName("Utils")

package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.classic.appendPrimitives
import it.unibo.tuprolog.solve.classic.appendRules
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor
import kotlin.jvm.JvmName

/** Converts each [Clause] to a [Rule] (see [ensureRules]) and wraps the result in a lazy [Cursor]. */
fun Sequence<Clause>.toRulesCursor(): Cursor<out Rule> = ensureRules().cursor()

/**
 * Casts every [Clause] in this sequence to a [Rule].
 * @throws IllegalArgumentException if any clause is a fact rather than a proper rule (i.e. `it.isRule` is `false`).
 */
fun Sequence<Clause>.ensureRules(): Sequence<Rule> =
    map {
        require(it.isRule)
        it.castToRule()
    }

/** Flattens a (possibly nested) `, `/2 tuple of goals into the sequence of its leaves, depth-first. */
fun Term.unfoldGoals(): Sequence<Term> =
    when {
        this.isTuple -> castToTuple().toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

/**
 * Turns this term into the [Cursor] of goals `StateGoalSelection` iterates over: [unfoldGoals] flattens
 * conjunctions, and every variable goal is wrapped into `call(X)` so it goes through the ISO callability check
 * before execution.
 */
fun Term.toGoals(): Cursor<out Term> =
    unfoldGoals()
        .map {
            when {
                it.isVar -> Struct.of("call", it)
                else -> it
            }
        }.cursor()

/** Pushes a new child frame onto the execution-context stack for the current goal, becoming its own [ClassicExecutionContext.goals]. */
fun ClassicExecutionContext.createChild(inferProcedureFromGoals: Boolean = true): ClassicExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        parent = this,
        depth = depth + 1,
        step = step + 1,
        relevantVariables = emptySet(),
    )
}

/**
 * Last-call-optimization counterpart of [createChild]: reuses the current frame for the current goal (incrementing
 * [ClassicExecutionContext.depth] without growing the [ClassicExecutionContext.parent] chain), used by
 * `StateRuleSelection` for tail calls to avoid an unbounded execution-context stack.
 */
fun ClassicExecutionContext.replaceWithChild(inferProcedureFromGoals: Boolean = true): ClassicExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        depth = depth + 1,
        step = step + 1,
    )
}

/**
 * Attaches [rules] as this context's [ClassicExecutionContext.rules] cursor and records a matching
 * [it.unibo.tuprolog.solve.classic.ChoicePointContext.Rules] onto [ClassicExecutionContext.choicePoints], so the
 * remaining candidates (if any) can be retried later on backtracking.
 */
fun ClassicExecutionContext.appendRulesAndChoicePoints(rules: Cursor<out Rule>): ClassicExecutionContext {
    val newChoicePointContext =
        if (rules.hasNext) {
            choicePoints.appendRules(rules.next, this)
        } else {
            choicePoints.appendRules(Cursor.empty(), this)
        }

    return copy(rules = rules, choicePoints = newChoicePointContext)
}

/** [Solve.Response] analogue of [appendRulesAndChoicePoints], recording a [it.unibo.tuprolog.solve.classic.ChoicePointContext.Primitives] instead. */
fun ClassicExecutionContext.appendPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
): ClassicExecutionContext {
    val newChoicePointContext =
        if (primitiveExecutions.hasNext) {
            choicePoints.appendPrimitives(primitiveExecutions.next, this)
        } else {
            choicePoints.appendPrimitives(Cursor.empty(), this)
        }

    return copy(primitives = primitiveExecutions, choicePoints = newChoicePointContext)
}

/** Combines [createChild] and [appendRulesAndChoicePoints]: the usual way `StateRuleSelection` moves into a non-tail rule call. */
fun ClassicExecutionContext.createChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true,
): ClassicExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

/** Combines [replaceWithChild] and [appendRulesAndChoicePoints]: how `StateRuleSelection` moves into a last-call-optimized (tail) rule call. */
fun ClassicExecutionContext.replaceWithChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true,
): ClassicExecutionContext {
    val tempExecutionContext = replaceWithChild(inferProcedureFromGoals)
    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

/** Combines [createChild] and [appendPrimitivesAndChoicePoints]: how `StatePrimitiveSelection` moves into a primitive call. */
fun ClassicExecutionContext.createChildAppendingPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
    inferProcedureFromGoals: Boolean = true,
): ClassicExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions)
}

/** Builds the [Solve.Request] passed to a `Primitive`'s `solve` from this context, [goal] and [signature], at [startTime]. */
fun ClassicExecutionContext.toRequest(
    goal: Struct,
    signature: Signature,
    startTime: TimeInstant,
) = Solve.Request(signature, goal.args, this, startTime)
