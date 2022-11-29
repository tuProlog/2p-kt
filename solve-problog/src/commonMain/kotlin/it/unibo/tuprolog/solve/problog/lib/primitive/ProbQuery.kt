package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSetConfig.toSolveOptions

/**
 * This primitive is the main entrypoint for probabilistic logic queries. The first argument
 * represents the [Numeric] probability of the query being true, the and the second argument is the
 * query goal.
 *
 * @author Jason Dellaluce
 */
internal object ProbQuery : QuaternaryRelation.WithoutSideEffects<ExecutionContext>(
    "${PREDICATE_PREFIX}_query"
) {
    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
        fourth: Term,
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        ensuringArgumentIsStruct(1)
        ensuringArgumentIsInstantiated(2)

        val queryWithEvidenceExplanation = Var.of("QueryWithEvidenceExplanation")
        val evidenceExplanation = Var.of("EvidenceExplanation")
        val solutions = subSolver().solve(
            Tuple.of(
                Struct.of(ProbSetConfig.functor, third),
                Struct.of(ProbSolveWithEvidence.functor, queryWithEvidenceExplanation, evidenceExplanation, second),
            ),
            third.toSolveOptions()
        )

        return sequence {
            for (solution in solutions) {
                if (solution.isHalt) throw solution.exception!!

                val queryWithEvidenceExplanationTerm = solution.substitution[queryWithEvidenceExplanation]
                val evidenceExplanationTerm = solution.substitution[evidenceExplanation]

                if (queryWithEvidenceExplanationTerm == null ||
                    queryWithEvidenceExplanationTerm !is ProbExplanationTerm ||
                    evidenceExplanationTerm == null ||
                    evidenceExplanationTerm !is ProbExplanationTerm
                ) {
                    yield(Substitution.failed())
                } else {
                    val queryWithEvidenceProbability = queryWithEvidenceExplanationTerm.explanation.probabilityOrNull
                    val evidenceProbability = evidenceExplanationTerm.explanation.probabilityOrNull
                    if (queryWithEvidenceProbability == null || evidenceProbability == null) {
                        yield(Substitution.failed())
                    } else {
                        val solutionSubstitution = solution.substitution.filter {
                            v, _ ->
                            v != evidenceExplanation && v != queryWithEvidenceExplanation
                        }
                        val probabilityTerm = Numeric.of(queryWithEvidenceProbability / evidenceProbability)
                        yield(
                            Substitution.of(
                                solutionSubstitution,
                                mgu(first, probabilityTerm),
                                mgu(fourth, queryWithEvidenceExplanationTerm),
                            )
                        )
                    }
                }
            }
        }
    }
}
