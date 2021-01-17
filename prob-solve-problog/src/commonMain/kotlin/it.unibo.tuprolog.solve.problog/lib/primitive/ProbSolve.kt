package it.unibo.tuprolog.solve.problog.lib.primitive

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.bdd.or
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.DD_VAR_NAME
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.rule.Prob
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object ProbSolve : BinaryRelation.WithoutSideEffects<ExecutionContext>("${PREDICATE_PREFIX}_solve") {

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term
    ): Sequence<Substitution> {
        ensuringArgumentIsInstantiated(1)
        ensuringArgumentIsCallable(1)
        val bddVar = Var.of(DD_VAR_NAME)
        val solutions = solve(Struct.of(Prob.FUNCTOR, bddVar, second)).toList()
        val error = solutions.asSequence().filterIsInstance<Solution.Halt>().firstOrNull()
        if (error != null) {
            return sequenceOf(Substitution.failed())
        }
        return if (!solutions.asSequence().filterIsInstance<Solution.Yes>().any()) {
            sequenceOf(
                Substitution.of( // Add implicit "No" solution
                    first mguWith ProblogObjectRef(BinaryDecisionDiagram.Terminal(false))
                )
            )
        } else {
            solutions
                .asSequence()
                .filterIsInstance<Solution.Yes>()
                .groupBy { it.substitution.filter { v, _ -> v != bddVar } }.map {
                    var totalBdd: BinaryDecisionDiagram<ProbTerm> = BinaryDecisionDiagram.Terminal(false)
                    for (solution in it.value) {
                        val solutionBddVar = solution.substitution[bddVar]
                        if (solutionBddVar is ProblogObjectRef) {
                            totalBdd = totalBdd or solutionBddVar.bdd
                        }
                    }
                    Substitution.of(it.key, first mguWith ProblogObjectRef(totalBdd))
                }
                .asSequence()
        }
    }
}
