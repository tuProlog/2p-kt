package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.and
import it.unibo.tuprolog.bdd.not
import it.unibo.tuprolog.bdd.or
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.DD_VAR_NAME
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EVIDENCE_PREDICATE
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.rule.Prob
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object ProbSolveEvidence : UnaryPredicate.NonBacktrackable<ExecutionContext>(
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
                val bddVar = Var.of(DD_VAR_NAME)
                val solutionsBdd = solve(Struct.of(Prob.FUNCTOR, bddVar, term!!))
                    .toList()
                    .map { s -> s.substitution[bddVar] }
                    .filterIsInstance<ProblogObjectRef>()
                    .map { t -> t.bdd }
                val solutionBdd = if (solutionsBdd.isEmpty()) {
                    BinaryDecisionDiagram.Terminal(false)
                } else {
                    solutionsBdd.reduce { acc, t -> t or acc }
                }
                if (truth!!.isTrue) {
                    solutionBdd
                } else {
                    solutionBdd.not()
                }
            }
            .toList()

        val objectRef = ProblogObjectRef(
            if (result.isEmpty()) {
                BinaryDecisionDiagram.Terminal(true)
            } else {
                result.reduce { acc, t -> t and acc }
            }
        )
        return replyWith(first mguWith objectRef)
    }
}
