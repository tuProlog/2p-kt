package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EVIDENCE_PREDICATE
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EXPLANATION_VAR_NAME
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.getSolverOptions

/**
 * This primitive computes an explanation by finding solutions for all the "evidence" predicates
 * present in the knowledge base, and bundles the result in a [ProbExplanationTerm] that is substituted
 * with the first parameter.
 *
 * The computation is performed as follows. First, the Prolog engine finds solutions for the "evidence"
 * predicate over the current knowledge base. Solutions of this first solver provide all the terms that
 * evidence data over the knowledge base, which can either be true or false data samples. Then, an explanation
 * is computed for each of these initial solutions by invoking [ProbSolve] by setting the solutions of
 * the first step as goals. Since we are just interested in finding explanations, all explanations resulting
 * from [ProbSolve]with different substitutions are reduced together with an [ProbExplanation.or] logical operation.
 * Finally, all [ProbExplanation]s found are reduced together by applying an [ProbExplanation.and] logical operation,
 * and the result is substituted with the first argument of the primitive. This last step is necessary because
 * it handles the case in which more than one evidence is present in the knowledge base, in which case they
 * should be handled in conjunction in order to find probabilistic query solutions.
 *
 * @author Jason Dellaluce
 */
internal object ProbSolveEvidence : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${PREDICATE_PREFIX}_solve_evidence"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val evidenceIndicator = Indicator.of(EVIDENCE_PREDICATE, 2)
        if (evidenceIndicator !in context.staticKb && evidenceIndicator !in context.dynamicKb) {
            return replyWith(mgu(first, ProbExplanationTerm(ProbExplanation.TRUE)))
        }

        val evidenceTermVar = Var.of("TermVar")
        val evidenceTruthVar = Var.of("TruthVar")
        val subQuery = Struct.of(EVIDENCE_PREDICATE, evidenceTermVar, evidenceTruthVar)
        val subOptions = context.getSolverOptions().setLimit(SolveOptions.ALL_SOLUTIONS).setLazy(false)
        val result = subSolver().solve(subQuery, subOptions)
            .filterIsInstance<Solution.Yes>()
            .toList()
            .map {
                val truth = it.substitution[evidenceTruthVar]
                val term = it.substitution[evidenceTermVar]

                if (term == null || term.isVar || truth == null || truth !is Truth) {
                    ProbExplanation.FALSE
                } else {
                    val explanationVar = Var.of(EXPLANATION_VAR_NAME)
                    val totalExplanation = solve(Struct.of(ProbSolve.functor, explanationVar, term))
                        .map { s -> s.substitution[explanationVar] }
                        .filterIsInstance<ProbExplanationTerm>()
                        .map { t -> t.explanation }
                        .reduce { acc, t ->
                            when {
                                t.probability == 1.0 -> acc
                                acc.probability == 1.0 -> t
                                else -> t or acc
                            }
                        }
                    if (truth.isTrue) totalExplanation else totalExplanation.not()
                }
            }
            .filter { it != ProbExplanation.FALSE }

        val resultExplanation = if (result.isEmpty()) ProbExplanation.TRUE else result.reduce { acc, t -> t and acc }
        return replyWith(mgu(first, ProbExplanationTerm(resultExplanation)))
    }
}
