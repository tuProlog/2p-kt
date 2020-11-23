package it.unibo.tuprolog.solve.problogimpl.fsm

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.appendPrimitives
import it.unibo.tuprolog.solve.appendRules
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.impl.ofTrueTerminal
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

fun ProblogClassicExecutionContext.createTempChild(inferProcedureFromGoals: Boolean = true): ProblogClassicExecutionContext {
    val currentGoal = this.currentGoal as Struct

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        parent = this,
        depth = depth + 1,
        step = step + 1,
        bdd = BinaryDecisionDiagram.ofTrueTerminal(),
        probRule = null,
    )
}

fun ProblogClassicExecutionContext.appendRulesAndChoicePoints(rules: Cursor<out Rule>): ProblogClassicExecutionContext {
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

fun ProblogClassicExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions: Cursor<out Solve.Response>): ProblogClassicExecutionContext {
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

fun ProblogClassicExecutionContext.createChildAppendingRulesAndChoicePoints(
    rules: Cursor<out Rule>,
    inferProcedureFromGoals: Boolean = true
): ProblogClassicExecutionContext {
    val tempExecutionContext = createTempChild(inferProcedureFromGoals)

    return tempExecutionContext.appendRulesAndChoicePoints(rules)
}

fun ProblogClassicExecutionContext.createChildAppendingPrimitivesAndChoicePoints(
    primitiveExecutions: Cursor<out Solve.Response>,
    inferProcedureFromGoals: Boolean = true
): ProblogClassicExecutionContext {
    val tempExecutionContext = createTempChild(inferProcedureFromGoals)

    return tempExecutionContext.appendPrimitivesAndChoicePoints(primitiveExecutions)
}

// TODO Giovanni's review needed!! with Git > Show History
fun ProblogClassicExecutionContext.toRequest(
    goal: Struct,
    signature: Signature
): Solve.Request<ProblogClassicExecutionContext> =
    Solve.Request(
        signature,
        goal.argsList,
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
