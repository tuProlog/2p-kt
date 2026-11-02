package it.unibo.tuprolog.solve.concurrent

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.Solution

fun concurrentSolutionPresentationContract() =
    concurrentContract {
        scenario("solution-presentation/solution-with-dandling-vars") {
            logicProgramming {
                val theory =
                    theoryOf(
                        fact { "append"("seq"(X), X) },
                    )
                val solver = solverOf(staticKb = theory)
                val query = "append"(A, B)
                val sol = solver.solveOnce(query)
                sol.shouldBeInstanceOf<Solution.Yes>()
                (setOf(A, B).all { it in sol.substitution.keys }).shouldBeTrue()
                (sol.substitution[A]!! matches "seq"(`_`)).shouldBeTrue()
                (sol.substitution[B] as? Var)?.name shouldBe "X"
            }
        }
    }
