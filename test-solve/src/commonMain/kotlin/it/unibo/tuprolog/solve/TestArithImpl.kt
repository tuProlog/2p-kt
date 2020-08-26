package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.collections.listOf as ktListOf

internal class TestArithImpl(private val solverFactory: SolverFactory) : TestArith {

    override fun testArithDiff() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "=\\="(0,1)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "=\\="(1.0,1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = "=\\="(3*2,7-1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = "=\\="("N",5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("=\\=", 3),
                                    varOf("N")
                            )
                    )
                    ),
                    solutions
            )

            query = "=\\="("floot"(1),5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("=\\=", 3),
                                    TypeError.Expected.EVALUABLE,
                                    varOf("floot/1")
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testArithEq() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "=:="(0,1)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = "=:="(1.0,1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "=:="(3*2,7-1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "=:="("N",5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("=:=", 3),
                                    varOf("N")
                            )
                    )
                    ),
                    solutions
            )

            query = "=:="("floot"(1),5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("=:=", 3),
                                    TypeError.Expected.EVALUABLE,
                                    varOf("floot/1")
                            )
                    )
                    ),
                    solutions
            )

            query =  ""("0.333 =:= 1/3")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testArithGreaterThan() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = ">"(0,1)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = ">"(1.0,1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = ">"(3*2,7-1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = ">"("X",5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature(">", 3),
                                    varOf("X")
                            )
                    )
                    ),
                    solutions
            )

            query = ">"(2 + "floot"(1),5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature(">", 3),
                                    TypeError.Expected.EVALUABLE,
                                    varOf("floot/1")
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testArithGreaterThanEq() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = ">="(0,1)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = ">="(1.0,1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = ">="(3*2,7-1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = ">="("X",5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature(">", 3),
                                    varOf("X")
                            )
                    )
                    ),
                    solutions
            )

            query = ">="(2 + "floot"(1),5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature(">", 3),
                                    TypeError.Expected.EVALUABLE,
                                    varOf("floot/1")
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testArithLessThan() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "<"(0,1)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "<"(1.0,1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = "<"(3*2,7-1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )

            query = "<"("X",5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("<", 3),
                                    varOf("X")
                            )
                    )
                    ),
                    solutions
            )

            query = "<"(2 + "floot"(1),5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("<", 3),
                                    TypeError.Expected.EVALUABLE,
                                    varOf("floot/1")
                            )
                    )
                    ),
                    solutions
            )
        }
    }

    override fun testArithLessThanEq() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "=<"(0,1)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "=<"(1.0,1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "=<"(3*2,7-1)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )

            query = "=<"("X",5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            InstantiationError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("=<", 3),
                                    varOf("X")
                            )
                    )
                    ),
                    solutions
            )

            query = "=<"(2 + "floot"(1),5)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.halt(
                            TypeError.forGoal(
                                    DummyInstances.executionContext,
                                    Signature("=<", 3),
                                    TypeError.Expected.EVALUABLE,
                                    varOf("floot/1")
                            )
                    )
                    ),
                    solutions
            )
        }
    }
}