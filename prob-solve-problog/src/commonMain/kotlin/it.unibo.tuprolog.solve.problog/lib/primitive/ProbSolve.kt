package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.EXPLANATION_VAR_NAME
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.rule.Prob
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

internal object ProbSolve : BinaryRelation.WithoutSideEffects<ExecutionContext>("${PREDICATE_PREFIX}_solve") {

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val explanationVar = Var.of(EXPLANATION_VAR_NAME)
        val solutions = solve(Struct.of(Prob.FUNCTOR, explanationVar, second)).toList()
        val error = solutions.asSequence().filterIsInstance<Solution.Halt>().firstOrNull()
        if (error != null) {
            return sequenceOf(Substitution.failed())
        }
        return if (!solutions.asSequence().filterIsInstance<Solution.Yes>().any()) {
            sequenceOf(
                Substitution.of( // Add implicit "No" solution
                    first mguWith ProbExplanationTerm(ProbExplanation.FALSE)
                )
            )
        } else {
            solutions
                .asSequence()
                .filterIsInstance<Solution.Yes>()
                .groupBy { it.substitution.filter { v, _ -> v != explanationVar } }.map {
                    var totalExplanation = ProbExplanation.FALSE
                    for (solution in it.value) {
                        val solutionExplanationVar = solution.substitution[explanationVar]
                        if (solutionExplanationVar is ProbExplanationTerm) {
                            totalExplanation = totalExplanation or solutionExplanationVar.explanation
                        }
                    }
                    Substitution.of(it.key, first mguWith ProbExplanationTerm(totalExplanation))
                }
                .asSequence()
        }
    }
}
