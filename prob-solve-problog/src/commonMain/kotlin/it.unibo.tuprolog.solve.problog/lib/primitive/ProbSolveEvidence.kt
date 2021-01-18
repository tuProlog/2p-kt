package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EXPLANATION_VAR_NAME
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EVIDENCE_PREDICATE
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.rule.Prob
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

internal object ProbSolveEvidence : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${PREDICATE_PREFIX}_solve_evidence"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val termVar = Var.of("TermVar")
        val truthVar = Var.of("TruthVar")
        val result = solve(
            Struct.of(
                ",",
                Struct.of(
                    EnsurePrologCall.functor,
                    Struct.of(EVIDENCE_PREDICATE, Var.of("A"), Var.of("B"))
                ),
                Struct.of(EVIDENCE_PREDICATE, termVar, truthVar)
            )
        )
            .filterIsInstance<Solution.Yes>()
            .map {
                val truth = it.substitution[truthVar]
                val term = it.substitution[termVar]
                val explanationVar = Var.of(EXPLANATION_VAR_NAME)
                val allSolutionsExplanation = solve(Struct.of(Prob.FUNCTOR, explanationVar, term!!))
                    .toList()
                    .map { s -> s.substitution[explanationVar] }
                    .filterIsInstance<ProbExplanationTerm>()
                    .map { t -> t.explanation }
                val solutionExplanation = if (allSolutionsExplanation.isEmpty()) {
                    ProbExplanation.FALSE
                } else {
                    allSolutionsExplanation.reduce { acc, t -> t or acc }
                }
                if (truth!!.isTrue) {
                    solutionExplanation
                } else {
                    solutionExplanation.not()
                }
            }
            .toList()

        val objectRef = ProbExplanationTerm(
            if (result.isEmpty()) {
                ProbExplanation.TRUE
            } else {
                result.reduce { acc, t -> t and acc }
            }
        )
        return replyWith(first mguWith objectRef)
    }
}
