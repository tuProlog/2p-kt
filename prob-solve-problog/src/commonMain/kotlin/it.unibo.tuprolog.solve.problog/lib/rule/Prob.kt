package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.primitive.EnsurePrologCall
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbBuildNot
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolve
import it.unibo.tuprolog.solve.rule.RuleWrapper

sealed class Prob : RuleWrapper<ClassicExecutionContext>(FUNCTOR, ARITY) {

    companion object {
        private val TRUE_TERMINAL = BinaryDecisionDiagram.Terminal<ProbTerm>(true)
        const val FUNCTOR: String = ProblogLib.PREDICATE_PREFIX
        const val ARITY: Int = 2
    }

    abstract override val Scope.head: List<Term>

    abstract override val Scope.body: Term

    sealed class Negation : Prob() {
        override val Scope.body: Term
            get() {
                val xVar = varOf("X")
                val posBddVar = varOf("${ProblogLib.DD_VAR_NAME}_POS")
                return tupleOf(
                    structOf("ground", xVar),
                    structOf(ProbSolve.functor, posBddVar, xVar),
                    atomOf("!"),
                    structOf(ProbBuildNot.functor, varOf(ProblogLib.DD_VAR_NAME), posBddVar),
                )
            }

        object Not : Negation() {
            override val Scope.head: List<Term>
                get() = ktListOf(
                    varOf(ProblogLib.DD_VAR_NAME),
                    structOf("not", varOf("X"))
                )
        }

        object NegationAsFailure : Negation() {
            override val Scope.head: List<Term>
                get() = ktListOf(
                    varOf(ProblogLib.DD_VAR_NAME),
                    structOf("\\+", varOf("X"))
                )
        }
    }

    object Prolog : Prob() {
        override val Scope.head: List<Term>
            get() = ktListOf(
                ProblogObjectRef(TRUE_TERMINAL),
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
