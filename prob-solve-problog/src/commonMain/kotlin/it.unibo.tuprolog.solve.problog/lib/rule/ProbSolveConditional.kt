package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbBuildAnd
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolve
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolveEvidence
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList

object ProbSolveConditional : RuleWrapper<ClassicExecutionContext>(
    "${PREDICATE_PREFIX}SolveConditional",
    3
) {

    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("QE"), varOf("E"), varOf("Q"))

    override val Scope.body: Term
        get() {
            val resultQ = varOf("RES_Q")
            val resultE = varOf("E")
            return tupleOf(
                structOf(ProbSolve.functor, resultQ, varOf("Q")),
                structOf(ProbSolveEvidence.functor, resultE),
                structOf(ProbBuildAnd.functor, varOf("QE"), resultE, resultQ)
            )
        }
}
