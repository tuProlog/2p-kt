package it.unibo.tuprolog.solve.problog.lib.rule

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProbTerm
import it.unibo.tuprolog.solve.problog.lib.knowledge.ProblogObjectRef
import it.unibo.tuprolog.solve.problog.lib.primitive.EnsureBuiltin
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbBuildAnd
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbBuildNot
import it.unibo.tuprolog.solve.problog.lib.primitive.ProbSolve
import it.unibo.tuprolog.solve.rule.RuleWrapper

sealed class Prob : RuleWrapper<ClassicExecutionContext>(FUNCTOR, ARITY) {

    companion object {
        private val TRUE_TERMINAL = BinaryDecisionDiagram.Terminal<ProbTerm>(true)
        const val FUNCTOR: String = ProblogLib.PREDICATE_PREFIX
        const val ARITY: Int = 2
        val SIGNATURE: Signature
            get() = Signature(FUNCTOR, ARITY)
    }

    abstract override val Scope.head: List<Term>

    abstract override val Scope.body: Term

    object And : Prob() {
        override val Scope.head: List<Term>
            get() = ktListOf(
                varOf(ProblogLib.SOLUTION_VAR_NAME),
                structOf(",", varOf("A"), varOf("B"))
            )

        override val Scope.body: Term
            get() {
                val bddA = varOf("${ProblogLib.SOLUTION_VAR_NAME}_A")
                val bddB = varOf("${ProblogLib.SOLUTION_VAR_NAME}_B")
                return tupleOf(
                    atomOf("!"),
                    structOf(functor, bddA, varOf("A")),
                    structOf(functor, bddB, varOf("B")),
                    structOf(ProbBuildAnd.functor, varOf(ProblogLib.SOLUTION_VAR_NAME), bddA, bddB),
                )
            }
    }

    object Or : Prob() {
        override val Scope.head: List<Term>
            get() = ktListOf(
                varOf(ProblogLib.SOLUTION_VAR_NAME),
                structOf(";", varOf("A"), varOf("B"))
            )

        override val Scope.body: Term
            get() {
                val bdd = varOf(ProblogLib.SOLUTION_VAR_NAME)
                return tupleOf(
                    atomOf("!"),
                    structOf(
                        ";",
                        structOf(functor, bdd, varOf("A")),
                        structOf(functor, bdd, varOf("B")),
                    )
                )
            }
    }

    object Arrow : Prob() {
        override val Scope.head: List<Term>
            get() = ktListOf(
                varOf(ProblogLib.SOLUTION_VAR_NAME),
                structOf("->", varOf("A"), varOf("B"))
            )

        override val Scope.body: Term
            get() = tupleOf(
                atomOf("!"),
                structOf(
                    "->",
                    varOf("A"),
                    structOf(functor, varOf(ProblogLib.SOLUTION_VAR_NAME), varOf("B")),
                )
            )
    }

    object Negation : Prob() {
        override val Scope.head: List<Term>
            get() = ktListOf(
                varOf(ProblogLib.SOLUTION_VAR_NAME),
                structOf("\\+", varOf("X"))
            )

        override val Scope.body: Term
            get() {
                val xVar = varOf("X")
                val posBddVar = varOf("${ProblogLib.SOLUTION_VAR_NAME}_POS")
                return tupleOf(
                    atomOf("!"),
                    structOf("ground", xVar),
                    structOf(ProbSolve.functor, posBddVar, xVar),
                    atomOf("!"),
                    structOf(ProbBuildNot.functor, varOf(ProblogLib.SOLUTION_VAR_NAME), posBddVar),
                )
            }
    }

    object Disjunction : Prob() {
        const val FUNCTOR = "${ProblogLib.PREDICATE_PREFIX}Disjunction"

        override val Scope.head: List<Term>
            get() = ktListOf(
                varOf(ProblogLib.SOLUTION_VAR_NAME),
                structOf(FUNCTOR, varOf("X"))
            )

        override val Scope.body: Term
            get() {
                val posBddVar = varOf("${ProblogLib.SOLUTION_VAR_NAME}_POS")
                return tupleOf(
                    atomOf("!"),
                    structOf(functor, posBddVar, varOf("X")),
                    structOf(ProbBuildNot.functor, varOf(ProblogLib.SOLUTION_VAR_NAME), posBddVar),
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
                    structOf(EnsureBuiltin.functor, xVar),
                    xVar,
                    atomOf("!")
                )
            }
    }
}
