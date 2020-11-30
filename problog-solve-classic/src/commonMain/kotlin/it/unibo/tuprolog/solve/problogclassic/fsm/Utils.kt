package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.problogclassic.appendPrimitives
import it.unibo.tuprolog.solve.problogclassic.appendRules
import it.unibo.tuprolog.solve.problogclassic.knowledge.ProblogRule
import it.unibo.tuprolog.solve.problogclassic.knowledge.ProblogSolutionTerm
import it.unibo.tuprolog.solve.problogclassic.problogAnd
import it.unibo.tuprolog.struct.BinaryDecisionDiagram
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

/** Each child context should have a brand new bdd.
 * Probabilistic terms involved in the resolution should be propagated in the child context.
 *
 * @author Jason Dellaluce */
fun ProblogClassicExecutionContext.createTempChild(inferProcedureFromGoals: Boolean = true): ProblogClassicExecutionContext {
    val currentGoal = this.currentGoal as Struct

    return copy(
        goals = currentGoal.toGoals(),
        procedure = if (inferProcedureFromGoals) currentGoal else procedure,
        parent = this,
        depth = depth + 1,
        step = step + 1,
        bdd = BinaryDecisionDiagram.Terminal(true),
        probRules = probRules
    )
}

/**
 * Gathers all probabilistic rules and facts in the current context and collects them in the
 * list that is propagated to child contexts during the resolution. Those will be used during
 * [BinaryDecisionDiagram] construction to have traceability of which probabilistic clause
 * led to a specific probabilistic solution term.
 *
 * @author Jason Dellaluce
 */
fun ProblogClassicExecutionContext.prepareProbRules(): ProblogClassicExecutionContext {
    val curRule = rules.current!!
    var newProbRules = probRules
    if (curRule is ProblogRule) {
        newProbRules = newProbRules.plus(curRule)
    }
    return copy(probRules = newProbRules, bdd = bdd)
}

/**
 * Uses the current substitution to ground as many probabilistic rules/facts as possible from the ones
 * collected in the resolution until this point. Every grounded probabilistic term gets removed from the
 * list and it's used to construct the solution [BinaryDecisionDiagram]. The probabilistic term list is so
 * reduced to the terms that still need further substitutions, which will keep being propagated.
 *
 * @author Jason Dellaluce
 */
fun ProblogClassicExecutionContext.groundProbTermsAndBuildDecisionDiagram(): ProblogClassicExecutionContext {
    /* Apply a substitution to the most recent Probabilistic Rule/Fact */
    var curBDD = bdd

    val newProbRules = probRules.filter {
        var subst = it[substitution]
        if (subst is Rule) {
            subst = subst.head
        }
        if (subst.isGround) {
            /* Map the result in a Probabilistic solved goals and rebuild the BDD */
            val solvedTerm = ProblogSolutionTerm(
                it.id,
                it.probability,
                subst,
            )
            curBDD = curBDD problogAnd BinaryDecisionDiagram.Var(solvedTerm)
            false
        } else {
            true
        }
    }
    return copy(bdd = curBDD, probRules = newProbRules)
}
