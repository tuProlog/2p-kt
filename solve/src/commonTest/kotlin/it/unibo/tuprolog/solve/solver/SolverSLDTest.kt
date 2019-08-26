package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.noResponseBy
import it.unibo.tuprolog.solve.solver.SolverUtils.orderedWithStrategy
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.SolverUtils.yesResponseBy
import it.unibo.tuprolog.solve.testutils.DummyInstances
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test

/**
 * Test class for [SolverSLD]
 *
 * @author Enrico
 */
internal class SolverSLDTest {

    @Test
    fun firstInformalTest() {
        val goal = Solve.Request(
                Signature("p", 1),
                listOf(Var.of("X")),
                ExecutionContext(
                        Libraries(Library.of(alias = "test", theory = ClauseDatabase.of(
                                Fact.of(Struct.of("p", Atom.of("a"))),
                                Fact.of(Struct.of("p", Atom.of("b"))),
                                Rule.of(Struct.of("p", Atom.of("b")), Truth.fail()),
                                Fact.of(Struct.of("p", Var.of("X"))),
                                Rule.of(Atom.of("p"), Atom.of("a")),
                                Rule.of(Atom.of("p"), Atom.of("c")),
                                Rule.of(Atom.of("p"), Truth.fail())
                        ))),
                        emptyMap(),
                        ClauseDatabase.of(),
                        ClauseDatabase.of()
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

    @Test
    fun prologStandardExample() {
        val database = ClauseDatabase.of(
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("q", varOf("X")),
                            structOf("r", varOf("X"), varOf("Y"))
                    )
                },
                Scope.empty().run {
                    ruleOf(structOf("p", varOf("X"), varOf("Y")),
                            structOf("s", varOf("X")))
                },
                Scope.empty().run { factOf(structOf("s", atomOf("d"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("a"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("b"))) },
                Scope.empty().run { factOf(structOf("q", atomOf("c"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("b"), atomOf("b1"))) },
                Scope.empty().run { factOf(structOf("r", atomOf("c"), atomOf("c1"))) }
        )

        // rude implementation of "," functor primitive
        val primitive: Primitive = { mainRequest ->
            when {
                mainRequest.signature.name == Tuple.FUNCTOR -> sequence {
                    val toEvalSubGoals = with(mainRequest) {
                        orderedWithStrategy(arguments.asSequence(), context, context.solverStrategies::predicationChoiceStrategy)
                    }

                    val leftSubSolveRequest = mainRequest.newSolveRequest(
                            prepareForExecution(toEvalSubGoals.first())
                    )

                    Solver.sld().solve(leftSubSolveRequest).forEach { leftResponse ->
                        when (leftResponse.solution) {
                            is Solution.Yes -> {
                                val rightSubSolveRequest = leftSubSolveRequest.newSolveRequest(
                                        prepareForExecution(toEvalSubGoals.last()
                                                .apply(leftResponse.solution.substitution))
                                )

                                Solver.sld().solve(rightSubSolveRequest).forEach { rightResponse ->
                                    when (rightResponse.solution) {
                                        is Solution.Yes -> yield(mainRequest.yesResponseBy(rightResponse))
                                        is Solution.No -> yield(mainRequest.noResponseBy(rightResponse))
                                    }
                                }
                            }
                            is Solution.No -> yield(mainRequest.noResponseBy(leftResponse))
                        }
                    }

                }
                else -> throw IllegalStateException("This primitive handles only ',' functor")
            }
        }

        val goal = Solve.Request(
                Signature("p", 2),
                listOf(Var.of("U"), Var.of("V")),
                DummyInstances.executionContext.copy(
                        libraries = Libraries(Library.of(
                                alias = "testLib",
                                theory = database,
                                primitives = mapOf(Signature(",", 2) to primitive)
                        ))
                )
        )

        Solver.sld().solve(goal).toList().forEach { println("$it\n") }
    }

}
