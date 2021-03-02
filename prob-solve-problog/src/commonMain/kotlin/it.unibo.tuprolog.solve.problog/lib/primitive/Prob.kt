package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.safeToStruct
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.toTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.withExplanation
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetMode.isPrologMode
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * This primitive represents a single goal in the probabilistic resolution. The first argument is a term representing
 * the [ProbExplanation] explanations of a goal's solutions, and the second argument represents the probabilistic
 * goal itself. The primitive returns a sequence containing all the substitutions for the goal, which are
 * all its solutions and their corresponding explanation in the form of [ProbExplanationTerm].
 *
 * The computation uses regular Prolog SLD resolution semantics. Solutions are not grouped and the resolution
 * uses a depth-first approach.
 *
 * This primitive also handles backwards compatibility with Prolog.
 *
 * It is worth mentioning that no probability is calculated by this primitive. Instead, this just bundles the logic
 * for finding probabilistic goal solutions and their explanation.
 *
 * @author Jason Dellaluce
 */
internal object Prob : BinaryRelation.WithoutSideEffects<ExecutionContext>(PREDICATE_PREFIX) {
    private val negationAsFailureFunctors = setOf("\\+", "not")

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val goal = second.safeToStruct()
        val goalSignature = goal.extractSignature()

        return sequence {
            /* Apply selective behavior based on goal's functor */
            when (goal.functor) {
                /* Edge case: Negation as failure */
                in negationAsFailureFunctors -> yieldAll(
                    /* Optimize Prolog-only queries */
                    if (context.isPrologMode()) {
                        solve(Struct.of("\\+", Struct.of(functor, Var.anonymous(), goal[0]))).map {
                            it.substitution + (first mguWith ProbExplanation.TRUE.toTerm())
                        }
                    } else {
                        solve(Struct.of(ProbNegationAsFailure.functor, first, goal[0])).map {
                            it.substitution
                        }
                    }
                )
                /* Edge case: call/1 */
                "call" -> yieldAll(
                    solve(Struct.of(goal.functor, goal[0].withExplanation(first))).map {
                        it.substitution
                    }
                )
                /* Edge case: findall/3 and findall/4 */
                "findall" -> {
                    val goalArgs = goal.args.copyOf()
                    goalArgs[1] = goalArgs[1].withExplanation(Var.anonymous())
                    yieldAll(
                        solve(Struct.of(goal.functor, *goalArgs)).map {
                            it.substitution + (first mguWith ProbExplanation.TRUE.toTerm())
                        }
                    )
                }
                /* Edge case: bagof/3 and bagof/4 */
                "bagof" -> {
                    val goalArgs = goal.args.copyOf()
                    goalArgs[1] = goalArgs[1].withExplanation(Var.anonymous())
                    yieldAll(
                        solve(Struct.of(goal.functor, *goalArgs)).map {
                            it.substitution + (first mguWith ProbExplanation.TRUE.toTerm())
                        }
                    )
                }
                /* Bottom-line general case */
                else -> {
                    /* Support for Prolog libraries backwards compatibility */
                    val isPrologOnly = first is Truth || goalSignature in context.libraries
                    if (isPrologOnly ||
                        goalSignature.toIndicator() in context.staticKb ||
                        goalSignature.toIndicator() in context.dynamicKb
                    ) {
                        yieldAll(
                            solve(goal).map {
                                it.substitution + (first mguWith ProbExplanation.TRUE.toTerm())
                            }
                        )
                    }

                    /* Solve probabilistic goal */
                    if (!isPrologOnly) {
                        yieldAll(solve(goal.withExplanation(first)).map { it.substitution })
                    }
                }
            }
        }
    }
}
