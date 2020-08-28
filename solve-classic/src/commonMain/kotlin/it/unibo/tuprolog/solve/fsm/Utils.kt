package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cursor


fun Sequence<Clause>.ensureRules(): Cursor<out Rule> =
    @Suppress("USELESS_CAST")
    map { require(it is Rule); it as Rule }.cursor()

fun Term.unfoldGoals(): Sequence<Term> =
    when (this) {
        is Tuple -> toSequence().flatMap { it.unfoldGoals() }
        else -> sequenceOf(this)
    }

fun Term.toGoals(): Cursor<out Term> =
    unfoldGoals().map {
        when (it) {
            is Var -> Struct.of("call", it)
            else -> it
        }
    }.cursor()

fun ClassicExecutionContext.createTempChild(inferProcedureFromGoals: Boolean = true): ClassicExecutionContext {
    val currentGoal = this.currentGoal as Struct

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        parent = this,
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

    return copy(
        rules = rules,
        choicePoints = newChoicePointContext
    )
}

fun ClassicExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions: Cursor<out Solve.Response>): ClassicExecutionContext {
    val newChoicePointContext = if (primitiveExecutions.hasNext) {
        choicePoints.appendPrimitives(primitiveExecutions.next, this)
    } else {
        choicePoints.appendPrimitives(Cursor.empty(), this)
    }

    return copy(
        primitives = primitiveExecutions,
        choicePoints = newChoicePointContext
    )
}

fun ClassicExecutionContext.createChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true
): ClassicExecutionContext {
    val tempExecutionContext = createTempChild(inferProcedureFromGoals)

    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

fun ClassicExecutionContext.createChildAppendingPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
    inferProcedureFromGoals: Boolean = true
): ClassicExecutionContext {
    val tempExecutionContext = createTempChild(inferProcedureFromGoals)

    return tempExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions)
}

// TODO Giovanni's review needed!! with Git > Show History
fun ClassicExecutionContext.toRequest(
    signature: Signature,
    arguments: kotlin.collections.List<Term>
): Solve.Request<ClassicExecutionContext> =
    Solve.Request(
        signature,
        arguments,
        copy(
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            substitution = substitution
        ),
        executionMaxDuration = maxDuration
    )