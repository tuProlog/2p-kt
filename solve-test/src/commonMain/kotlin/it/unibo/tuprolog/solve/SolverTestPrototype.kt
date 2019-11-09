package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.prologStandardExampleDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.prologStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.prologStandardExampleWithCutDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.prologStandardExampleWithCutDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabaseNotableGoalToSolutions
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.collections.listOf as ktListOf

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

    /** Test `true` goal */
    fun testTrue() {
        prolog {
            val solver = solverOf()
            val query = truthOf(true)
            val solutions = solver.solve(truthOf(true)).toList()

            assertSolutionEquals(
                    ktListOf(query.yesSolution()),
                    solutions
            )
        }
    }

    /** Test with [simpleFactDatabaseNotableGoalToSolutions] */
    fun testUnification() {
        prolog {
            val solver = solverOf(staticKB = simpleFactDatabase)

            simpleFactDatabaseNotableGoalToSolutions.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [simpleCutDatabaseNotableGoalToSolutions] */
    fun testSimpleCutAlternatives() {
        prolog {
            val solver = solverOf(staticKB = simpleCutDatabase)

            simpleCutDatabaseNotableGoalToSolutions.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [simpleCutAndConjunctionDatabaseNotableGoalToSolutions] */
    fun testCutAndConjunction() {
        prolog {
            val solver = solverOf(staticKB = simpleCutAndConjunctionDatabase)

            simpleCutAndConjunctionDatabaseNotableGoalToSolutions.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions] */
    fun testCutConjunctionAndBacktracking() {
        prolog {
            val solver = solverOf(staticKB = cutConjunctionAndBacktrackingDatabase)

            cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [infiniteComputationDatabaseNotableGoalToSolution] */
    fun testMaxDurationParameterAndTimeOutException() {
        prolog {
            val solver = solverOf(staticKB = infiniteComputationDatabase)

            infiniteComputationDatabaseNotableGoalToSolution.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal, maxDuration = 100L).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [prologStandardExampleDatabaseNotableGoalToSolution] */
    fun testPrologStandardSearchTreeExample() {
        prolog {
            val solver = solverOf(staticKB = prologStandardExampleDatabase)

            prologStandardExampleDatabaseNotableGoalToSolution.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [prologStandardExampleWithCutDatabaseNotableGoalToSolution] */
    fun testPrologStandardSearchTreeWithCutExample() {
        prolog {
            val solver = solverOf(staticKB = prologStandardExampleWithCutDatabase)

            prologStandardExampleWithCutDatabaseNotableGoalToSolution.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    /** Test with [customReverseListDatabaseNotableGoalToSolution] */
    fun testBacktrackingWithCustomReverseListImplementation() {
        prolog {
            val solver = solverOf(staticKB = customReverseListDatabase)

            customReverseListDatabaseNotableGoalToSolution.forEach { (goal, solutionList) ->
                val solutions = solver.solve(goal).toList()

                assertSolutionEquals(solutionList, solutions)
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

    fun testBasicBacktracking1() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                            fact { "b"(1) },
                            rule { "b"(2) impliedBy "!" },
                            fact { "b"(3) },
                            fact { "c"(2) },
                            fact { "c"(3) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(2).toList()

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(2), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertEquals(numOf(2), it.substitution["N"])
            }
        }
    }

    fun testBasicBacktracking2() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy ("c"("X") and "b"("X")) },
                            rule { "b"(2) impliedBy "!" },
                            fact { "b"(3) },
                            fact { "c"(3) },
                            fact { "c"(2) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(3).toList()

            assertEquals(2, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(3), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertEquals(numOf(3), it.substitution["N"])
            }

            solutions[1].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(2), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertEquals(numOf(2), it.substitution["N"])
            }
        }
    }

    fun testBasicBacktracking3() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy (("b"("X") and "!") and "c"("X")) },
                            fact { "b"(2) },
                            fact { "b"(3) },
                            fact { "c"(2) },
                            fact { "c"(3) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(2).toList()

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(2), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertEquals(numOf(2), it.substitution["N"])
            }
        }
    }

    fun testBasicBacktracking4() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a"("X") impliedBy ("b"("X") and ("!" and "c"("X"))) },
                            fact { "b"(2) },
                            fact { "b"(3) },
                            fact { "c"(2) },
                            fact { "c"(3) }
                    )
            )

            val solutions = solver.solve("a"("N")).take(2).toList()

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(2), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertEquals(numOf(2), it.substitution["N"])
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

    fun testConjunctionOfConjunctions() {
        prolog {
            val solver = solverOf(
                    staticKB = theoryOf(
                            rule { "a" impliedBy (tupleOf("b", "c") and tupleOf("d", "e")) },
                            fact { "b" },
                            fact { "c" },
                            fact { "d" },
                            fact { "e" }
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
                assertEquals(numOf(1), it.substitution["N"])
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
                assertEquals(numOf(1), it.substitution["N"])
            }

            solutions[1].let {
                assertTrue { it is Solution.Yes }
                assertEquals("a"("N"), it.query)
                assertEquals("a"(2), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
                assertTrue { "N" in it.substitution }
                assertEquals(numOf(2), it.substitution["N"])
            }
        }
    }

    fun testMember() {
        prolog {
            val solver = solverOf()

            val constants = arrayOf("a", "b", "c")
            val goal = "member"("X", listOf(*constants))

            val solutions = solver.solve(goal).take(constants.size + 2).toList()

            assertEquals(constants.size + 1, solutions.size)

            solutions.last().let {
                assertTrue { it is Solution.No }
                assertEquals(goal, it.query)
                assertTrue { it.substitution is Substitution.Fail }
                assertNull(it.solvedQuery)
            }

            for (i in constants.indices) {
                solutions[i].let {
                    assertTrue { it is Solution.Yes }
                    assertEquals(goal, it.query)
                    assertEquals("member"(constants[i], listOf(*constants)), it.solvedQuery)
                    assertTrue { it.substitution is Substitution.Unifier }
                    assertTrue { "X" in it.substitution }
                    assertEquals(atomOf(constants[i]), it.substitution["X"])
                }
            }
        }
    }
}