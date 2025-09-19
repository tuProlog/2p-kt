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

fun Sequence<Clause>.ensureRules(): Sequence<Rule> =
    map {
        require(it.isRule)
        it.castToRule()
    }

fun Term.unfoldGoals(): Sequence<Term> =
    when {
        this.isTuple -> castToTuple().toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

fun Term.toGoals(): Cursor<out Term> =
    unfoldGoals()
        .map {
            when {
                it.isVar -> Struct.of("call", it)
                else -> it
            }
        }.cursor()

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

fun ConcurrentExecutionContext.replaceWithChild(inferProcedureFromGoals: Boolean = true): ConcurrentExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        depth = depth + 1,
        step = step + 1,
    )
}

fun ConcurrentExecutionContext.createChildAppendingRules(
    rule: Rule,
    inferProcedureFromGoals: Boolean = true,
): ConcurrentExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.copy(rule = rule)
}

fun ConcurrentExecutionContext.replaceWithChildAppendingRules(
    rule: Rule,
    inferProcedureFromGoals: Boolean = true,
): ConcurrentExecutionContext {
    val tempExecutionContext = replaceWithChild(inferProcedureFromGoals)
    return tempExecutionContext.copy(rule = rule)
}

fun ConcurrentExecutionContext.toRequest(
    goal: Struct,
    signature: Signature,
    startTime: TimeInstant,
) = Solve.Request(signature, goal.args, this, startTime)
