package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.problog.lib.ProblogLib.PREDICATE_PREFIX
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbCalc
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList

object ProbQuery : RuleWrapper<ClassicExecutionContext>("${PREDICATE_PREFIX}Query", 2) {

    override val Scope.head: KtList<Term>
        get() = ktListOf(varOf("A"), varOf("B"))

    override val Scope.body: Term
        get() {
            val resultQE = varOf("QE")
            val resultE = varOf("E")
            val probQE = varOf("PROB_QE")
            val probE = varOf("PROB_E")
            return tupleOf(
                structOf(ProbSolveConditional.functor, resultQE, resultE, varOf("B")),
                structOf(ProbCalc.functor, probQE, resultQE),
                structOf(ProbCalc.functor, probE, resultE),
                structOf(
                    "is",
                    varOf("A"),
                    structOf(
                        "/",
                        probQE,
                        probE
                    ),
                ),
            )
//            val bddVar = varOf(SOLUTION_VAR_NAME)
//            return tupleOf(
//                atomOf("!"),
//                structOf(ProbSolve.functor, bddVar, varOf("B")),
//                structOf(ProbCalc.functor, varOf("A"), bddVar)
//            )
        }
}
