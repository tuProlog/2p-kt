package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.wrapInPredicate
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * This is analogous to [ProbSolve], but it takes in consideration "evidence" predicates.
 * The third argument is the probabilistic goal to be solved, the second is the explanation of the conjunction
 * of all the evidences in the knowledge base, and the first is the conjunction of explanations of goal's solutions
 * and evidences. Explanations are substituted in the form of [ProbExplanationTerm].
 *
 * @author Jason Dellaluce
 */
internal object ProbSolveWithEvidence : TernaryRelation.WithoutSideEffects<ExecutionContext>(
    "${PREDICATE_PREFIX}_solve_with_evidence"
) {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(2)
        ensuringArgumentIsCallable(2)

        val evidenceExplanationVar = Var.of("EvidenceExplanation")
        val evidenceExplanationTerm = context.createSolver()
            .solve(Struct.of(ProbSolveEvidence.functor, evidenceExplanationVar))
            .first().substitution[evidenceExplanationVar]

        val goalExplanationVar = Var.of("GoalExplanation")
        val solutions = solve(third.wrapInPredicate(ProbSolve.functor, goalExplanationVar))
        return sequence {
            for (solution in solutions) {
                val goalExplanationTerm = solution.substitution[goalExplanationVar]
                if (evidenceExplanationTerm == null ||
                        evidenceExplanationTerm !is ProbExplanationTerm ||
                        goalExplanationTerm == null ||
                        goalExplanationTerm !is ProbExplanationTerm) {
                    yield(Substitution.failed())
                } else {
                    val explanationWithEvidenceTerm = ProbExplanationTerm(
                        goalExplanationTerm.explanation and evidenceExplanationTerm.explanation)
                    yield(Substitution.Companion.of(
                        first mguWith explanationWithEvidenceTerm,
                        second mguWith evidenceExplanationTerm,
                        solution.substitution.filter { v, _ -> v != goalExplanationVar}
                    ))
                }
            }
        }
    }
}
