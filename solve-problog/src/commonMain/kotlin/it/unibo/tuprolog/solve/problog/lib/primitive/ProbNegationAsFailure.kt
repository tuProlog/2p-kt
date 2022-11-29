package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.setProbabilistic

/**
 * This primitive executes negation-as-failure calls for probabilistic goals. In probabilistic logic goal resolution, it
 * is not sufficient to find at least one true/false example to solve a negation predicate, because that
 * would not be enough to compute the complementary probability of the goal itself. For that purpose,
 * we should find all the solutions of the goal and negate their explanations.
 *
 * @author Jason Dellaluce
 */
internal object ProbNegationAsFailure : BinaryRelation.NonBacktrackable<ExecutionContext>(
    "${ProblogLib.PREDICATE_PREFIX}_negation_as_failure"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)

        return if (!second.isGround) {
            replyException(
                ResolutionException(
                    "Probabilistic negation does not support non-ground goals: $second",
                    context = context
                )
            )
        } else {
            val explanationTermVar = Var.of("Explanation")
            val solution = subSolver().solve(
                Struct.of(ProbSolve.functor, explanationTermVar, second),
                SolveOptions.DEFAULT.setProbabilistic(false)
            ).firstOrNull()
            if (solution == null) {
                replyFail()
            } else {
                val explanationTerm = solution.substitution[explanationTermVar]
                if (explanationTerm == null || explanationTerm !is ProbExplanationTerm) {
                    replyException(
                        ResolutionException(
                            "No valid explanation has been solved for goal: $second",
                            context = context
                        )
                    )
                } else {
                    replyWith(
                        Substitution.of(
                            mgu(first, ProbExplanationTerm(explanationTerm.explanation.not()))
                        )
                    )
                }
            }
        }
    }
}
