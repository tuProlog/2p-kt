package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestArgImpl(
    private val solverFactory: SolverFactory,
) : TestArg {
    override fun testArgFromFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testArgFromFooX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "a")),
                solutions,
            )
        }
    }

    override fun testArgFromFoo2() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "foo"("X", "b"), "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "a")),
                solutions,
            )
        }
    }

    override fun testArgFromFooInF() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(2, "foo"("a", "f"("X", "b"), "c"), "f"("a", "Y"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    listOf(
                        yes("X" to "a", "Y" to "b"),
                    )
                },
                solutions,
            )
        }
    }

    override fun testArgFromFooY() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "foo"("X", "b"), "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("Y" to "X")),
                solutions,
            )
        }
    }

    override fun testArgFromFooInSecondTerm() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "b")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testArgFromFooInFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testArgNumberFromFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(3, "foo"(3, 4), "N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testArgXFromFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg("X", "foo"("a", "b"), "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes("X" to 1),
                ),
                solutions,
            )
        }
    }

    override fun testArgNumberFromX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(1, "X", "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            varOf("X"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArgFromAtom() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(0, "atom", "A")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            TypeError.Expected.COMPOUND,
                            atomOf("atom"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArgFromNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(0, 3, "A")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            TypeError.Expected.COMPOUND,
                            numOf(3),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testNegativeArgFromFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg(intOf(-3), "foo"("a", "b"), "A")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            DomainError.Expected.NOT_LESS_THAN_ZERO,
                            numOf(-3),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testArgAFromFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = arg("a", "foo"("a", "b"), "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
