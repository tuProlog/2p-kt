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
        val termAsStruct = second.safeToStruct()
        val termSignature = termAsStruct.extractSignature()

        return sequence {
            /* Support to probabilistic negation as failure */
            if (termAsStruct.functor in negationAsFailureFunctors) {
                yieldAll(
                    /* Optimize Prolog-only queries */
                    if (context.isPrologMode()) {
                        solve(Struct.of("\\+", Struct.of(Prob.functor, Var.anonymous(), termAsStruct[0]))).map {
                            it.substitution + (first mguWith ProbExplanation.TRUE.toTerm())
                        }
                    } else {
                        solve(Struct.of(ProbNegationAsFailure.functor, first, termAsStruct[0])).map {
                            it.substitution
                        }
                    }
                )
            } else {
                /* Support for Prolog libraries backwards compatibility */
                val isPrologOnly = first is Truth || termSignature in context.libraries
                if (isPrologOnly ||
                    termSignature.toIndicator() in context.staticKb ||
                    termSignature.toIndicator() in context.dynamicKb
                ) {
                    yieldAll(
                        solve(termAsStruct).map {
                            it.substitution + (first mguWith ProbExplanation.TRUE.toTerm())
                        }
                    )
                }

                /* Solve probabilistic goal */
                if (!isPrologOnly) {
                    yieldAll(solve(termAsStruct.withExplanation(first)).map { it.substitution })
                }
            }
        }
    }
}
