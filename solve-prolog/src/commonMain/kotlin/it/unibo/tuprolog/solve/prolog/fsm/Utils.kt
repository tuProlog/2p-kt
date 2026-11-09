@file:JvmName("Utils")

package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.prolog.ReadOnlyExecutionContext
import it.unibo.tuprolog.solve.prolog.appendPrimitives
import it.unibo.tuprolog.solve.prolog.appendRules
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
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

/** Pushes a new child frame onto the execution-context stack for the current goal, becoming its own [PrologExecutionContext.goals]. */
fun PrologExecutionContext.createChild(inferProcedureFromGoals: Boolean = true): PrologExecutionContext {
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
 * [PrologExecutionContext.depth] without growing the [PrologExecutionContext.parent] chain), used by
 * `StateRuleSelection` for tail calls to avoid an unbounded execution-context stack.
 */
fun PrologExecutionContext.replaceWithChild(inferProcedureFromGoals: Boolean = true): PrologExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        depth = depth + 1,
        step = step + 1,
    )
}

/**
 * Attaches [rules] as this context's [PrologExecutionContext.rules] cursor and records a matching
 * [it.unibo.tuprolog.solve.prolog.ChoicePointContext.Rules] onto [PrologExecutionContext.choicePoints], so the
 * remaining candidates (if any) can be retried later on backtracking.
 */
fun PrologExecutionContext.appendRulesAndChoicePoints(rules: Cursor<out Rule>): PrologExecutionContext {
    val alternatives = if (rules.hasNext) rules.next else Cursor.empty()
    val newChoicePointContext = choicePoints.appendRules(alternatives, this)
    if (alternatives.hasNext && !procedure.isControlConstruct) counters?.countChoicePoint(open = 0)
    return copy(rules = rules, choicePoints = newChoicePointContext)
}

/** [Solve.Response] analogue of [appendRulesAndChoicePoints], recording a [it.unibo.tuprolog.solve.prolog.ChoicePointContext.Primitives] instead. */
fun PrologExecutionContext.appendPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
): PrologExecutionContext {
    val alternatives = if (primitiveExecutions.hasNext) primitiveExecutions.next else Cursor.empty()
    val newChoicePointContext = choicePoints.appendPrimitives(alternatives, this)
    // reading hasNext of an already computed cursor pulls nothing more; open = 0: the chain also holds exhausted
    // entries (one per call), so counting the open ones would walk all of it
    if (alternatives.hasNext && !procedure.isControlConstruct) counters?.countChoicePoint(open = 0)
    return copy(primitives = primitiveExecutions, choicePoints = newChoicePointContext)
}

/** Combines [createChild] and [appendRulesAndChoicePoints]: the usual way `StateRuleSelection` moves into a non-tail rule call. */
fun PrologExecutionContext.createChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true,
): PrologExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

/** Combines [replaceWithChild] and [appendRulesAndChoicePoints]: how `StateRuleSelection` moves into a last-call-optimized (tail) rule call. */
fun PrologExecutionContext.replaceWithChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true,
): PrologExecutionContext {
    val tempExecutionContext = replaceWithChild(inferProcedureFromGoals)
    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

/** Combines [createChild] and [appendPrimitivesAndChoicePoints]: how `StatePrimitiveSelection` moves into a primitive call. */
fun PrologExecutionContext.createChildAppendingPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
    inferProcedureFromGoals: Boolean = true,
): PrologExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions)
}

/**
 * Builds the [Solve.Request] passed to a `Primitive`'s `solve` from this context, [goal] and [signature], carrying a
 * read-only view of this context.
 */
fun PrologExecutionContext.toRequest(
    goal: Struct,
    signature: Signature,
): Solve.Request<ExecutionContext> = Solve.Request(signature, goal.args, ReadOnlyExecutionContext(this))

/**
 * Whether this goal is an ISO control construct (`,`/2, `;`/2, `->`/2, `call/N`, `catch/3`, `throw/1`), or the
 * callable check `call/1` runs first ([EnsureExecutable]), which [it.unibo.tuprolog.solve.ResolutionCounters] don't
 * count: this engine runs them as library predicates, other engines natively, and the counts must not depend on that.
 */
internal val Struct?.isControlConstruct: Boolean
    get() =
        this != null &&
            when (functor) {
                "call" -> true
                ",", ";", "->" -> arity == 2
                "catch" -> arity == 3
                "throw" -> arity == 1
                EnsureExecutable.functor -> arity == 1
                else -> false
            }

/** Records a predicate call to [goal] at [depth] in [counters], unless it is a control construct. */
internal fun PrologExecutionContext.countInference(goal: Struct) {
    if (counters != null && !goal.isControlConstruct) counters.countInference(depth + 1)
}

/** Records an alternative of the call to [procedure] in [counters], unless it is a control construct. */
internal fun PrologExecutionContext.countAlternative() {
    if (counters != null && !procedure.isControlConstruct) counters.countAlternative()
}
