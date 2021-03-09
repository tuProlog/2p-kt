package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.safeToStruct
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.toTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.withBodyExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.withExplanation
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetMode.isPrologMode
import it.unibo.tuprolog.solve.problog.lib.rules.Prob
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

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
    private val negationAsFailureFunctors = setOf("\\+", "not")

    private val recursiveGoalFunctors = setOf(",", ";", "->")

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
                in negationAsFailureFunctors -> yield(
                    /* Optimize Prolog-only queries */
                    if (context.isPrologMode()) {
                        (third mguWith Struct.of("\\+", Struct.of(functor, Var.anonymous(), goal[0]))) +
                            (first mguWith ProbExplanation.TRUE.toTerm())
                    } else {
                        third mguWith Struct.of(ProbNegationAsFailure.functor, first, goal[0])
                    }
                )
                /* Edge case: The current goal is a conjunction/disjunction or any sort of recursive predicate.
                * NOTE: This is not supposed to trigger regularly because we map the theory prior to query execution,
                * however this happens when the current goal is the initial query itself. As such, we want recursive
                * predicates in queries to be supported. */
                in recursiveGoalFunctors -> yield(
                    /* Optimize Prolog-only queries */
                    if (context.isPrologMode()) {
                        (third mguWith goal.withBodyExplanation(Var.anonymous())) +
                            (first mguWith ProbExplanation.TRUE.toTerm())
                    } else {
                        third mguWith goal.withBodyExplanation(first)
                    }
                )
                /* Edge case: call/1 predicate*/
                "call" -> yield(third mguWith Struct.of(goal.functor, goal[0].withExplanation(first)))
                /* Edge case: findall/3 and findall/4 */
                "findall" -> {
                    val goalArgs = goal.args.copyOf()
                    goalArgs[1] = goalArgs[1].withExplanation(Var.anonymous())
                    yield(
                        (third mguWith Struct.of(goal.functor, *goalArgs)) +
                            (first mguWith ProbExplanation.TRUE.toTerm())
                    )
                }
                /* Edge case: bagof/3 and bagof/4 */
                "bagof" -> {
                    val goalArgs = goal.args.copyOf()
                    goalArgs[1] = goalArgs[1].withExplanation(Var.anonymous())
                    yield(
                        (third mguWith Struct.of(goal.functor, *goalArgs)) +
                            (first mguWith ProbExplanation.TRUE.toTerm())
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
                        yield((third mguWith goal) + (first mguWith ProbExplanation.TRUE.toTerm()))
                    }

                    /* Solve probabilistic goal */
                    if (!isPrologOnly) {
                        yield(third mguWith goal.withExplanation(first))
                    }
                }
            }
        }
    }
}
