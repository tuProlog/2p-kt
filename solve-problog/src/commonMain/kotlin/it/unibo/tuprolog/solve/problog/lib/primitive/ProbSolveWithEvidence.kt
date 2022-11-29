package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.impl.toTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.getSolverOptions
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.isPrologMode

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
        val evidenceExplanationTerm = if (context.isPrologMode()) {
            /* No need to compute evidence for Prolog-only queries */
            ProbExplanation.TRUE.toTerm()
        } else {
            val subQuery = Struct.of(ProbSolveEvidence.functor, evidenceExplanationVar)
            val solution = subSolver().solveOnce(subQuery, context.getSolverOptions())
            solution.substitution[evidenceExplanationVar]
        }

        val goalExplanationVar = Var.of("GoalExplanation")
        val solutions = subSolver().solve(
            Struct.of(ProbSolve.functor, goalExplanationVar, third),
            context.getSolverOptions()
        )
        return sequence {
            for (solution in solutions) {
                if (solution.isHalt) throw solution.exception!!

                val goalExplanationTerm = solution.substitution[goalExplanationVar]
                if (evidenceExplanationTerm == null ||
                    evidenceExplanationTerm !is ProbExplanationTerm ||
                    goalExplanationTerm == null ||
                    goalExplanationTerm !is ProbExplanationTerm
                ) {
                    yield(Substitution.failed())
                } else {
                    val explanationWithEvidenceTerm = ProbExplanationTerm(
                        goalExplanationTerm.explanation and evidenceExplanationTerm.explanation
                    )
                    yield(
                        Substitution.of(
                            mgu(first, explanationWithEvidenceTerm),
                            mgu(second, evidenceExplanationTerm),
                            solution.substitution.filter { v, _ -> v != goalExplanationVar }
                        )
                    )
                }
            }
        }
    }
}
