package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.solve.libraryOf
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

class TestSubstitutionMerging {
    private val theory =
        logicProgramming {
            theoryOf(
                rule { "a"(X) impliedBy "b"(X) },
                rule { "b"(Y) impliedBy "c"(Y) },
                rule { "c"(Z) impliedBy "primitive"(Z) },
            )
        }

    @Suppress("ktlint:standard:multiline-expression-wrapping")
    private val primitive =
        object : UnaryPredicate<ExecutionContext>("primitive") {
            override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> =
                sequence {
                    val allVariables = context.substitution.let {
                        it.keys.toSet() + it.values.filterIsInstance<Var>().toSet()
                    }
                    for (x in allVariables.sortedBy { it.name }) {
                        yield(replySuccess(Substitution.unifier(x to Atom.of(x.name))))
                    }
                }
        }

    @Test
    fun testSubstitutionsAreCorrectlyMergedAfterPrimitives() {
        logicProgramming {
            val solver =
                ClassicSolverFactory
                    .newBuilder()
                    .staticKb(theory)
                    .noBuiltins()
                    .runtime(libraryOf("test", primitive).toRuntime())
                    .build()

            val goal = "a"(A)

            val solutions = solver.solveList(goal)
            assertSolutionEquals(
                listOf(
                    goal.yes(A to atomOf("A")),
                    goal.yes(A to atomOf("X")),
                    goal.yes(A to atomOf("Y")),
                    goal.yes(A to atomOf("Z")),
                ),
                solutions,
            )
        }
    }
}
