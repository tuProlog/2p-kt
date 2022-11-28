package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.stdlib.rule.Catch
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.warning.MissingPredicate
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.LastCallOptimization.ON
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.buffered

data class StateRuleSelection(override val context: ConcurrentExecutionContext) : AbstractState(context) {

    companion object {
        private val catchSignature = Catch.signature
    }

    private fun exceptionalState(exception: ResolutionException): StateException =
        StateException(exception, context.copy(step = nextStep()))

    private fun missingProcedure(ruleSources: Sequence<Theory>, missing: Signature): State =
        when (val unknown = context.flags[Unknown]) {
            Unknown.FAIL -> failureState
            else -> {
                if (ruleSources.none { missing.toIndicator() in it }) {
                    when (unknown) {
                        Unknown.ERROR -> exceptionalState(
                            ExistenceError.forProcedure(
                                context = context,
                                procedure = missing
                            )
                        )
                        Unknown.WARNING -> failureState.also {
                            context.warnings.write(
                                MissingPredicate(
                                    context = context,
                                    signature = missing
                                )
                            )
                        }
                        else -> failureState
                    }
                } else {
                    failureState
                }
            }
        }

    private val ignoreState: StateGoalSelection
        get() = StateGoalSelection(context.copy(goals = context.goals.next, step = nextStep()))

    private val failureState: EndState
        get() = StateEnd(
            solution = Solution.no(context.query),
            context = context.copy(step = nextStep())
        )

    override fun computeNext(): Iterable<State> {
        val currentGoal = context.currentGoal!!
        return when {
            currentGoal.isVar ->
                listOf(
                    exceptionalState(
                        InstantiationError.forGoal(
                            context = context,
                            procedure = context.procedure!!.extractSignature(),
                            variable = currentGoal.castToVar()
                        )
                    )
                )
            currentGoal.isStruct -> with(context) {
                val currentGoalStruct = currentGoal.castToStruct()
                val ruleSources = sequenceOf(libraries.asTheory(context.unificator), staticKb, dynamicKb)
                when {
                    currentGoalStruct.isTruth -> {
                        listOf(
                            if (currentGoalStruct.isTrue) {
                                ignoreState
                            } else {
                                failureState
                            }
                        )
                    }
                    ruleSources.any { currentGoalStruct in it } -> {
                        val rules = ruleSources.flatMap { it.selectClauses(currentGoalStruct) }
                        rules.map {
                            if (context.isTailRecursive) {
                                StateRuleExecution(context.replaceWithChildAppendingRules(it))
                            } else {
                                StateRuleExecution(context.createChildAppendingRules(it))
                            }
                        }.asIterable()
                    }
                    else -> listOf(missingProcedure(ruleSources, currentGoalStruct.extractSignature()))
                }
            }
            else -> listOf(
                exceptionalState(
                    TypeError.forGoal(
                        context = context,
                        procedure = context.procedure!!.extractSignature(),
                        expectedType = TypeError.Expected.CALLABLE,
                        culprit = currentGoal
                    )
                )
            )
        }
    }

    private val ConcurrentExecutionContext.isTailRecursive: Boolean
        get() = goals.next.isOver && flags[LastCallOptimization] == ON && goals.current!!.let { currentGoal ->
            currentGoal.asStruct()?.extractSignature()?.let {
                it == procedure?.extractSignature() && it != catchSignature
            } ?: false
        }

    private fun Theory.selectClauses(term: Struct): Sequence<Rule> =
        get(term).map { it.freshCopy() }.ensureRules().let {
            if (isMutable) {
                it.buffered()
            } else {
                it
            }
        }

    override fun clone(context: ConcurrentExecutionContext): StateRuleSelection = copy(context = context)
}
