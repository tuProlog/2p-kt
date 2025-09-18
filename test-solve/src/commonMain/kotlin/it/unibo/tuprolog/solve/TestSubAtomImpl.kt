package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestSubAtomImpl(
    private val solverFactory: SolverFactory,
) : TestSubAtom {
    override fun testSubAtomSubIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("abracadabra", intOf(0), intOf(1), intOf(10), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("S" to atomOf("a"))),
                solutions,
            )
        }
    }

    override fun testSubAtomSubIsVar2() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("abracadabra", intOf(6), intOf(5), intOf(0), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("S" to atomOf("dabra"))),
                solutions,
            )
        }
    }

    override fun testSubAtomSubIsVar3() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("abracadabra", intOf(3), "L", intOf(3), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("S" to atomOf("acada")), query.yes("L" to 5)),
                solutions,
            )
        }
    }

    override fun testSubAtomDoubleVar4() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("banana", intOf(3), intOf(2), "T", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("S" to atomOf("an")), query.yes("T" to 1)),
                solutions,
            )
        }
    }

    override fun testSubAtomInstantiationError() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("Banana", intOf(3), intOf(2), "Y", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("sub_atom", 5),
                            varOf("Banana"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testSubAtomTypeErrorAtomIsInteger() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = sub_atom(5, 2, 2, "_", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("sub_atom", 5),
                            TypeError.Expected.ATOM,
                            Integer.of("5"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    // sub_atom('Banana', 4, 2, _, 2).
    override fun testSubAtomTypeErrorSubIsInteger() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = sub_atom("banana", 4, 2, "_", 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("sub_atom", 5),
                            TypeError.Expected.ATOM,
                            Integer.of("2"),
                            index = 4,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    // [sub_atom('Banana', a, 2, _, S2), type_error(integer,a)].
    override fun testSubAtomTypeErrorBeforeIsNotInteger() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = sub_atom("banana", "a", 2, "_", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("sub_atom", 5),
                            TypeError.Expected.INTEGER,
                            Atom.of("a"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testSubAtomTypeErrorLengthIsNotInteger() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = sub_atom("banana", 4, "n", "_", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("sub_atom", 5),
                            TypeError.Expected.INTEGER,
                            Atom.of("n"),
                            index = 2,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testSubAtomTypeErrorAfterIsNotInteger() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = sub_atom("banana", 4, 2, "m", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("sub_atom", 5),
                            TypeError.Expected.INTEGER,
                            Atom.of("m"),
                            index = 3,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
