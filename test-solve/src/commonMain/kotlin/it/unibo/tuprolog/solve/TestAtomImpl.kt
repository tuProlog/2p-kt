package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.collections.listOf as ktListOf

internal class TestAtomImpl(private val solverFactory: SolverFactory) : TestAtom {

    override fun testAtomAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomString() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("string")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomAofB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("Var")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(emptyList)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(6)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomNumDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomChars() {
        prolog {
            ktListOf(
                atom_chars("X", listOf("t", "e", "s", "t")).hasSolutions({ yes("X" to "test") }),
                atom_chars("test", listOf("t", "e", "s", "t")).hasSolutions({ yes() }),
                atom_chars("test", listOf("t", "e", "s", "T")).hasSolutions({ yes("T" to "t") }),
                atom_chars("test1", listOf("t", "e", "s", "T")).hasSolutions({ no() })
            )
        }
    }

    override fun testAtomCodes() {
        prolog {
            ktListOf(
                atom_codes("abc", "X")
                    .hasSolutions({ yes("X" to listOf(97, 98, 99)) }),
                atom_codes("test", "X")
                    .hasSolutions({ yes("X" to listOf(116, 101, 115, 116)) }),
                atom_codes("X", listOf(97, 98, 99))
                    .hasSolutions({ yes("X" to "abc") }),
                atom_codes("test", listOf(116, 101, 115, 116))
                    .hasSolutions({ yes() }),
                atom_codes("test", listOf(112, 101, 115, 116))
                    .hasSolutions({ no() })
            )
        }
    }

    override fun testAtomLength() {
        prolog {
            ktListOf(
                atom_length("test", intOf(4)).hasSolutions({ yes() }),
                atom_length("test", "X").hasSolutions({ yes("X" to 4) }),
                atom_length(
                    "X",
                    intOf(5)
                ).hasSolutions({
                    halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("atom_length", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                }),
                atom_length("testLength", "X").hasSolutions({ yes("X" to 10) }),
                atom_length("test", intOf(5)).hasSolutions({ no() })
            )
        }
    }

    override fun testAtomConcat() {
        prolog {
            ktListOf(
                atom_concat("test", "concat", "X")
                    .hasSolutions({ yes("X" to atomOf("testconcat")) }),
                atom_concat("test", "concat", "test")
                    .hasSolutions({ no() }),
                atom_concat("test", "X", "testTest")
                    .hasSolutions({ yes("X" to atomOf("Test")) }),
                atom_concat("X", "query", "testquery")
                    .hasSolutions({ yes("X" to atomOf("test")) })
            )
        }
    }

    override fun testCharCode() {
        prolog {
            ktListOf(
                char_code("a", "X").hasSolutions({ yes("X" to 97) }),
                char_code("X", intOf(97)).hasSolutions({ yes("X" to "a") }),
                char_code("g", intOf(104)).hasSolutions({ no() }),
                char_code("X", "a").hasSolutions({
                    halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("char_code", 2),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 1
                        )
                    )
                }),
                char_code("g", intOf(103)).hasSolutions({ yes() })
            )
        }
    }
}
