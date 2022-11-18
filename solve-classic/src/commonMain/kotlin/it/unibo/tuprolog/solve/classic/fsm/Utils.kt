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

fun Sequence<Clause>.toRulesCursor(): Cursor<out Rule> =
    ensureRules().cursor()

fun Sequence<Clause>.ensureRules(): Sequence<Rule> =
    map { require(it.isRule); it.castToRule() }

fun Term.unfoldGoals(): Sequence<Term> =
    when {
        this.isTuple -> castToTuple().toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

fun Term.toGoals(): Cursor<out Term> =
    unfoldGoals().map {
        when {
            it.isVar -> Struct.of("call", it)
            else -> it
        }
    }.cursor()

fun ClassicExecutionContext.createChild(inferProcedureFromGoals: Boolean = true): ClassicExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        parent = this,
        depth = depth + 1,
        step = step + 1,
        relevantVariables = emptySet()
    )
}

fun ClassicExecutionContext.replaceWithChild(inferProcedureFromGoals: Boolean = true): ClassicExecutionContext {
    val currentGoal = this.currentGoal!!.castToStruct()

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        depth = depth + 1,
        step = step + 1
    )
}

fun ClassicExecutionContext.appendRulesAndChoicePoints(rules: Cursor<out Rule>): ClassicExecutionContext {
    val newChoicePointContext = if (rules.hasNext) {
        choicePoints.appendRules(rules.next, this)
    } else {
        choicePoints.appendRules(Cursor.empty(), this)
    }

    return copy(rules = rules, choicePoints = newChoicePointContext)
}

fun ClassicExecutionContext.appendPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>
): ClassicExecutionContext {
    val newChoicePointContext = if (primitiveExecutions.hasNext) {
        choicePoints.appendPrimitives(primitiveExecutions.next, this)
    } else {
        choicePoints.appendPrimitives(Cursor.empty(), this)
    }

    return copy(primitives = primitiveExecutions, choicePoints = newChoicePointContext)
}

fun ClassicExecutionContext.createChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true
): ClassicExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

fun ClassicExecutionContext.replaceWithChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true
): ClassicExecutionContext {
    val tempExecutionContext = replaceWithChild(inferProcedureFromGoals)
    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

fun ClassicExecutionContext.createChildAppendingPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
    inferProcedureFromGoals: Boolean = true
): ClassicExecutionContext {
    val tempExecutionContext = createChild(inferProcedureFromGoals)
    return tempExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions)
}

fun ClassicExecutionContext.toRequest(
    goal: Struct,
    signature: Signature,
    startTime: TimeInstant
) = Solve.Request(signature, goal.args, this, startTime)
