package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.primitive.Signature
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SolverTestPrototype(solverFactory: SolverFactory) : SolverFactory by solverFactory {

    fun testBuiltinApi() {
        prolog {
            val solver = solverOf()

            solver.libraries.let { builtins ->
                assertTrue { Signature("!", 0) in builtins }
                assertTrue { Signature("call", 1) in builtins }
                assertTrue { Signature("catch", 3) in builtins }
                assertTrue { Signature("throw", 1) in builtins }
                assertTrue { Signature(",", 2) in builtins }
                assertTrue { Signature(";", 2) in builtins }
                assertTrue { Signature("->", 2) in builtins }
                assertTrue { Signature("\\+", 1) in builtins }
                assertTrue { Signature("not", 1) in builtins }
                assertTrue { Signature(">", 2) in builtins }
                assertTrue { Signature(">=", 2) in builtins }
                assertTrue { Signature("<", 2) in builtins }
                assertTrue { Signature("=<", 2) in builtins }
                assertTrue { Signature("=", 2) in builtins }
                assertTrue { Signature("==", 2) in builtins }
                assertTrue { Signature("\\=", 2) in builtins }
                assertTrue { Signature("\\==", 2) in builtins }
                assertTrue { Signature("member", 2) in builtins }
            }

        }
    }

    fun testFailure() {
        prolog {
            val solver = solverOf()

            val solutions = solver.solve(atomOf("a")).take(2).toList()

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.No }
                assertEquals(atomOf("a"), it.query)
                assertNull(it.solvedQuery)
                assertTrue { it.substitution is Substitution.Fail }
            }
        }
    }

    fun testConjunction() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a" impliedBy ("b" and "c") },
                            fact { "b" },
                            fact { "c" }
                    )
            )

            val solutions = solver.solve(atomOf("a")).take(2).toList()

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals(atomOf("a"), it.query)
                assertEquals(atomOf("a"), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
            }
        }
    }

    fun testConjunctionWithUnification() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                            fact { "b"(1) },
                            fact { "c"(1) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(2).toList()

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(1), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertTrue { "N" in it.substitution }
                assertEquals(numOf(1), it.substitution.getDeeply("N"))
            }
        }
    }

    fun testDisjunction() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a" impliedBy ("b" or "c") },
                            fact { "b" },
                            fact { "c" }
                    )
            )

            val solutions = solver.solve(atomOf("a")).take(3).toList()

            assertEquals(2, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals(atomOf("a"), it.query)
                assertEquals(atomOf("a"), it.solvedQuery)
            }

            solutions[1].let {
                assertTrue { it is Solution.Yes }
                assertEquals(atomOf("a"), it.query)
                assertEquals(atomOf("a"), it.solvedQuery)
            }
        }
    }

    fun testDisjunctionWithUnification() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy ("b"("X") or "c"("X")) },
                            fact { "b"(1) },
                            fact { "c"(2) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(3).toList()

            assertEquals(2, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(1), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertTrue { "N" in it.substitution }
                assertEquals(numOf(1), it.substitution.getDeeply("N"))
            }

            solutions[1].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(2), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertTrue { "N" in it.substitution }
                assertEquals(numOf(2), it.substitution.getDeeply("N"))
            }
        }
    }

}