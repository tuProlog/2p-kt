package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.classic.ChoicePointContext
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.classic.stdlib.rule.Catch
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.warning.MissingPredicate
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.LastCallOptimization.ON
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.cursor

data class StateRuleSelection(override val context: ClassicExecutionContext) : AbstractState(context) {

    companion object {
        private val catchSignature = Catch.signature

        private val transparentToCut = setOf(
            Signature(",", 2),
            Signature(";", 2),
            Signature("->", 2)
        )

        private sealed class CutLimit {
            abstract val depthToCut: Int
            abstract val procedure: Struct?

            open val isNone: Boolean
                get() = false

            object None : CutLimit() {
                override val depthToCut: Int = -1
                override val procedure: Struct? = null

                override val isNone: Boolean
                    get() = true
            }

            class Actual(override val depthToCut: Int, override val procedure: Struct?) : CutLimit()
        }
    }

    private val failureState: StateBacktracking
        get() = StateBacktracking(context.copy(step = nextStep()))

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
        get() = StateGoalSelection(
            context.copy(goals = context.goals.next, step = nextStep())
        )

    private fun Term.isCut(): Boolean = this.isAtom && this.castToAtom().value == "!"

    private fun ClassicExecutionContext.computeCutLimit(magicCut: Boolean = false): CutLimit {
        val cutLimit = if (magicCut) {
            this.pathToRoot.firstOrNull()
        } else {
            this.pathToRoot.firstOrNull { it.procedure?.extractSignature() !in transparentToCut }
        }
        return if (cutLimit == null) {
            CutLimit.None
        } else {
            CutLimit.Actual(cutLimit.depth, cutLimit.procedure)
        }
    }

    private fun cutCanBeSkipped(cutLimit: CutLimit, executionContext: ClassicExecutionContext): Boolean =
        cutLimit.isNone ||
            cutLimit.depthToCut > executionContext.depth ||
            cutLimit.depthToCut == executionContext.depth &&
            cutLimit.procedure != executionContext.procedure

    private fun ChoicePointContext?.performCut(cutLimit: CutLimit): ChoicePointContext? =
        when {
            this === null -> {
                null
            }
            cutCanBeSkipped(cutLimit, executionContext!!) -> {
                this
            }
            else -> {
                val cutCandidates = pathToRoot.filter {
                    it.executionContext!!.procedure == cutLimit.procedure
                }
                if (cutCandidates.any()) {
                    cutCandidates.firstOrNull {
                        it.executionContext!!.depth <= cutLimit.depthToCut
                    }?.parent
                } else {
                    this
                }
            }
        }

    private val ClassicExecutionContext.isTailRecursive: Boolean
        get() = goals.next.isOver && flags[LastCallOptimization] == ON && currentGoal!!.let { currentGoal ->
            currentGoal.asStruct()?.extractSignature()?.let {
                it == procedure?.extractSignature() && it != catchSignature
            } ?: false
        }

    override fun computeNext(): State {
        val currentGoal = context.currentGoal!!
        return when {
            currentGoal.isVar -> {
                exceptionalState(
                    InstantiationError.forGoal(
                        context = context,
                        procedure = context.procedure!!.extractSignature(),
                        variable = currentGoal.castToVar()
                    )
                )
            }
            currentGoal.isStruct -> with(context) {
                val currentGoalStruct = currentGoal.castToStruct()
                val ruleSources = sequenceOf(libraries.theory, staticKb, dynamicKb)

                when {
                    currentGoalStruct.isTruth -> {
                        if (currentGoalStruct.isTrue) {
                            ignoreState
                        } else {
                            failureState
                        }
                    }

                    currentGoalStruct.isCut() -> {
                        val cutLimit = computeCutLimit(currentGoalStruct is MagicCut)
                        ignoreState.let {
                            it.copy(
                                context = it.context.copy(
                                    choicePoints = it.context.choicePoints.performCut(cutLimit)
                                )
                            )
                        }
                    }

                    ruleSources.any { currentGoalStruct in it } -> {
                        val rules = ruleSources.flatMap { it.selectClauses(currentGoalStruct) }.cursor()
                        if (context.isTailRecursive) {
                            StateRuleExecution(context.replaceWithChildAppendingRulesAndChoicePoints(rules))
                        } else {
                            StateRuleExecution(context.createChildAppendingRulesAndChoicePoints(rules))
                        }
                    }

                    else -> missingProcedure(ruleSources, currentGoalStruct.extractSignature())
                }
            }
            else -> {
                exceptionalState(
                    TypeError.forGoal(
                        context = context,
                        procedure = context.procedure!!.extractSignature(),
                        expectedType = TypeError.Expected.CALLABLE,
                        culprit = currentGoal
                    )
                )
            }
        }
    }

    private fun Theory.selectClauses(term: Struct): Sequence<Rule> =
        get(term).map { it.freshCopy() }.ensureRules().let {
            if (isMutable) {
                it.buffered()
            } else {
                it
            }
        }

    override fun clone(context: ClassicExecutionContext): StateRuleSelection = copy(context = context)
}
