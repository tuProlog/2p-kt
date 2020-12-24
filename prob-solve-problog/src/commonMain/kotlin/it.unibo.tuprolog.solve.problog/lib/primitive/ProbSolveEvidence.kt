package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.bdd.applyAnd
import it.unibo.tuprolog.bdd.applyNot
import it.unibo.tuprolog.bdd.applyOr
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EVIDENCE_PREDICATE
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.SOLUTION_VAR_NAME
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.rule.Prob
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object ProbSolveEvidence : UnaryPredicate.NonBacktrackable<ExecutionContext>(
    "${PREDICATE_PREFIX}SolveEvidence"
) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val termVar = Var.of("TermVar")
        val truthVar = Var.of("TruthVar")
        val evidenceSolutions = solve(Struct.of(EVIDENCE_PREDICATE, termVar, truthVar)).toList()
        val result = evidenceSolutions.map {
            val truth = it.substitution[truthVar]
            val term = it.substitution[termVar]
            val bddVar = Var.of(SOLUTION_VAR_NAME)
            val solutionsBdd = solve(Struct.of(Prob.FUNCTOR, bddVar, term!!))
                .toList()
                .map { s -> s.substitution[bddVar] }
                .filterIsInstance<ProblogObjectRef>()
                .map { t -> t.bdd }
                .reduce { acc, t -> t applyOr acc }
            if (truth!!.isTrue) {
                solutionsBdd
            } else {
                solutionsBdd.applyNot()
            }
        }.reduce { acc, t -> t applyAnd acc }
        return replyWith(first mguWith ProblogObjectRef(result))
    }
}
