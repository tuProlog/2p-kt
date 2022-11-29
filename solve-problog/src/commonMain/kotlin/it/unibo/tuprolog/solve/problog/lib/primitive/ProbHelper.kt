package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.ClauseMappingUtils
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.safeToStruct
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.toTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.withBodyExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.withExplanation
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.isPrologMode
import it.unibo.tuprolog.solve.problog.lib.rules.Prob

/**
 * This primitive is an accessory helper for the [Prob] rule. Some internal logic is applied to the given goal
 * in order to respect the computation semantics and apply performance optimizations. Similarly to [Prob],
 * the first argument is a term representing the [ProbExplanation] explanations of a goal's solutions,
 * and the second argument represents the probabilistic goal itself. The third argument represents the output goal
 * obtained by applying the internal mapping logic. The primitive returns a sequence containing all the substitutions
 * for the output goal.
 *
 * This is only supposed to be inside [Prob].
 *
 * @author Jason Dellaluce
 */
internal object ProbHelper : TernaryRelation.WithoutSideEffects<ExecutionContext>("${PREDICATE_PREFIX}_helper") {

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val goal = second.safeToStruct()
        val goalSignature = goal.extractSignature()

        return sequence {
            /* Apply selective behavior based on goal's functor */
            when (goal.functor) {
                /* Edge case: Negation as failure */
                "\\+", "not" -> yield(
                    /* Optimize Prolog-only queries */
                    if (context.isPrologMode()) {
                        mgu(third, Struct.of(goal.functor, Struct.of(Prob.functor, Var.anonymous(), goal[0]))) +
                            mgu(first, ProbExplanation.TRUE.toTerm())
                    } else {
                        mgu(third, Struct.of(ProbNegationAsFailure.functor, first, goal[0]))
                    }
                )
                /* Edge case: The current goal is a conjunction/disjunction or any sort of recursive predicate.
                * NOTE: This is not supposed to trigger regularly because we map the theory prior to query execution,
                * however this happens when the current goal is the initial query itself. As such, we want recursive
                * predicates in queries to be supported. */
                ",", ";", "->" -> yield(
                    /* Optimize Prolog-only queries */
                    if (context.isPrologMode()) {
                        mgu(third, goal.withBodyExplanation(Var.anonymous())) +
                            mgu(first, ProbExplanation.TRUE.toTerm())
                    } else {
                        mgu(third, goal.withBodyExplanation(first))
                    }
                )
                /* Edge case: call/1 predicate*/
                "call" -> {
                    val newGoal = goal[0]
                    yield(
                        mgu(
                            third,
                            Tuple.of(
                                Struct.of("ensure_executable", newGoal),
                                Struct.of(goal.functor, newGoal.withBodyExplanation(first))
                            )
                        )
                    )
                }
                /* Edge case: catch/3 predicate*/
                "catch" -> {
                    yield(
                        mgu(
                            third,
                            Struct.of(
                                goal.functor,
                                goal[0].withBodyExplanation(first),
                                goal[1],
                                goal[2].withBodyExplanation(first)
                            )
                        )
                    )
                }
                /* Edge case: findall/3, findall/4. NOTE: Should we handle `bagof` too? */
                "findall" -> {
                    val goalArgs = goal.args.toTypedArray()
                    goalArgs[1] = goalArgs[1].withBodyExplanation(Var.anonymous())
                    yield(
                        mgu(third, Struct.of(goal.functor, *goalArgs)) +
                            mgu(first, ProbExplanation.TRUE.toTerm())
                    )
                }
                /* Edge case: assert family */
                "assert", "asserta", "assertz" -> {
                    val subGoal = goal[0]
                    val subGoalSignature = when (subGoal) {
                        is Clause -> subGoal.head!!.extractSignature()
                        else -> subGoal.safeToStruct().extractSignature()
                    }
                    if (subGoalSignature in context.libraries || (subGoal is Clause && !subGoal.isWellFormed)) {
                        yield(mgu(third, goal) + mgu(first, ProbExplanation.TRUE.toTerm()))
                    } else {
                        val newGoals = when (subGoal) {
                            is Clause -> ClauseMappingUtils.map(subGoal)
                            is Struct -> ClauseMappingUtils.map(Fact.of(subGoal)).map {
                                it.head!!
                            }
                            else -> listOf(subGoal)
                        }
                        yieldAll(
                            newGoals.map {
                                mgu(third, Struct.of(goal.functor, it)) + mgu(first, ProbExplanation.TRUE.toTerm())
                            }
                        )
                    }
                }
                /* Edge case: retract family */
                "retract", "retractall" -> {
                    val goalArg = goal[0]
                    yield(
                        mgu(
                            third,
                            Struct.of(
                                goal.functor,
                                if (goalArg.isClause ||
                                    (goalArg is Struct && goalArg.functor == ":-") ||
                                    goalArg.safeToStruct().extractSignature() in context.libraries
                                ) {
                                    goalArg
                                } else goalArg.withExplanation(first)
                            )
                        )
                    )
                }
                /* Edge case: clause */
                "clause" -> {
                    val subGoal = goal[0]
                    if (subGoal !is Struct || context.libraries.hasProtected(subGoal.extractSignature())) {
                        yield(mgu(third, goal) + mgu(first, ProbExplanation.TRUE.toTerm()))
                    } else {
                        yield(
                            mgu(
                                third,
                                Struct.of(
                                    goal.functor,
                                    goal[0].withExplanation(first),
                                    goal[1]
                                )
                            )
                        )
                    }
                }
                /* Bottom-line general case */
                else -> {
                    /* Support for Prolog libraries backwards compatibility */
                    val isPrologOnly = second is Truth || goalSignature in context.libraries
                    if (isPrologOnly ||
                        goalSignature.toIndicator() in context.staticKb ||
                        goalSignature.toIndicator() in context.dynamicKb
                    ) {
                        yield(mgu(third, goal) + mgu(first, ProbExplanation.TRUE.toTerm()))
                    }

                    /* Solve probabilistic goal */
                    if (!isPrologOnly) {
                        yield(mgu(third, goal.withExplanation(first)))
                    }
                }
            }
        }
    }
}
