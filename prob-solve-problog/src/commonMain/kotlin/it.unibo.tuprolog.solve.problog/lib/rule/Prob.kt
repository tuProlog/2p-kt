package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanation
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbExplanationTerm
import it.unibo.tuprolog.solve.problog.lib.primitive.EnsurePrologCall
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbExplNot
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolve
import it.unibo.tuprolog.solve.rule.RuleWrapper

internal sealed class Prob : RuleWrapper<ClassicExecutionContext>(FUNCTOR, ARITY) {

    companion object {
        private val TRUE_TERMINAL = ProbExplanation.TRUE
        const val FUNCTOR: String = ProblogLib.PREDICATE_PREFIX
        const val ARITY: Int = 2
    }

    abstract override val Scope.head: List<Term>

    abstract override val Scope.body: Term

    sealed class Negation : Prob() {
        override val Scope.body: Term
            get() {
                val xVar = varOf("X")
                val positiveExplanationVar = varOf("${ProblogLib.EXPLANATION_VAR_NAME}_POS")
                return tupleOf(
                    structOf("ground", xVar),
                    structOf(ProbSolve.functor, positiveExplanationVar, xVar),
                    atomOf("!"),
                    structOf(ProbExplNot.functor, varOf(ProblogLib.EXPLANATION_VAR_NAME), positiveExplanationVar),
                )
            }

        object Not : Negation() {
            override val Scope.head: List<Term>
                get() = ktListOf(
                    varOf(ProblogLib.EXPLANATION_VAR_NAME),
                    structOf("not", varOf("X"))
                )
        }

        object NegationAsFailure : Negation() {
            override val Scope.head: List<Term>
                get() = ktListOf(
                    varOf(ProblogLib.EXPLANATION_VAR_NAME),
                    structOf("\\+", varOf("X"))
                )
        }
    }

    object Prolog : Prob() {
        override val Scope.head: List<Term>
            get() = ktListOf(
                ProbExplanationTerm(TRUE_TERMINAL),
                varOf("X"),
            )

        override val Scope.body: Term
            get() {
                val xVar = varOf("X")
                return tupleOf(
                    structOf(EnsurePrologCall.functor, xVar),
                    atomOf("!"),
                    xVar,
                )
            }
    }
}
