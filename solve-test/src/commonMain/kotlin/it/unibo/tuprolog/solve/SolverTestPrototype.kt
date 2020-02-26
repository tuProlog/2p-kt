package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.CustomDatabases.ifThen1ToSolution
import it.unibo.tuprolog.solve.CustomDatabases.ifThen2ToSolution
import it.unibo.tuprolog.solve.CustomDatabases.ifThenDatabase1
import it.unibo.tuprolog.solve.CustomDatabases.ifThenDatabase2
import it.unibo.tuprolog.solve.CustomDatabases.ifThenElse1ToSolution
import it.unibo.tuprolog.solve.CustomDatabases.ifThenElse2ToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.callStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.callStandardExampleDatabaseGoalsToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.catchAndThrowStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.catchAndThrowStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.conjunctionStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.conjunctionStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.ifThenElseStandardExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.ifThenStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.ifThenStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.notStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.notStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleWithCutDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleWithCutDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.allPrologTestingDatabasesToRespectiveGoalsAndSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.callTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.catchTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.customRangeListGeneratorDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.customRangeListGeneratorDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.haltTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.replaceAllFunctors
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingPrimitives.timeLibrary
import it.unibo.tuprolog.solve.TimeRelatedDatabases.lessThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.slightlyMoreThan1100MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.slightlyMoreThan1800MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.slightlyMoreThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedDatabases.timeRelatedDatabase
import it.unibo.tuprolog.solve.exception.TimeOutException
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.collections.listOf as ktListOf

/** A prototype class for testing solver implementations */
class SolverTestPrototype(solverFactory: SolverFactory) : SolverFactory by solverFactory {

    /** Utility method to solve goals in [goalToSolutions] with [solver] and check if solutions are as expected by means of [assertSolutionEquals] */
    private fun assertSolverSolutionsCorrect(
        solver: Solver,
        goalToSolutions: List<Pair<Struct, List<Solution>>>,
        maxDuration: TimeDuration
    ) {
        goalToSolutions.forEach { (goal, solutionList) ->
            // solver.logDatabases()
            val solutions = solver.solve(goal, maxDuration).toList()
            assertSolutionEquals(solutionList, solutions)
            // logGoalAndSolutions(goal, solutions)
        }
    }

    /** Utility function to log loaded Solver databases */
    private fun Solver.logDatabases() {
        println(if (staticKB.clauses.any()) staticKB.toString(true) else "")
        println(if (dynamicKB.clauses.any()) dynamicKB.toString(true) else "")
    }

    /** Utility function to log passed goal and solutions */
    private fun logGoalAndSolutions(goal: Struct, solutions: Iterable<Solution>) {
        println("?- ${goal}.")
        solutions.forEach {
            when (it) {
                is Solution.Yes -> {
                    println("yes.\n\t${it.solvedQuery}")
                    it.substitution.forEach { vt -> println("\t${vt.key} / ${vt.value}") }
                }
                is Solution.Halt -> println("halt.\n\t${it.exception}")
                is Solution.No -> println("no.")
            }
        }
        println("".padEnd(80, '-'))
    }

    /** Test presence of correct built-ins */
    fun testBuiltinApi() {
        prolog {
            val solver = solverOf()

            solver.libraries.let { builtins ->
                assertTrue { Signature("!", 0) in builtins }
                assertTrue { Signature("call", 1) in builtins }
                assertTrue { Signature("catch", 3) in builtins }
                assertTrue { Signature("throw", 1) in builtins }
                assertTrue { Signature("halt", 0) in builtins }
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
            val solutions = solver.solve(query).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    /** Test with [lessThan500MsGoalToSolution] */
    fun testTimeout1() {
        assertSolverSolutionsCorrect(
            solver = solverOf(
                staticKB = timeRelatedDatabase,
                libraries = defaultLibraries + timeLibrary
            ),
            goalToSolutions = lessThan500MsGoalToSolution,
            maxDuration = 400L
        )
    }

    /** Test with [slightlyMoreThan500MsGoalToSolution] */
    fun testTimeout2() {
        assertSolverSolutionsCorrect(
            solver = solverOf(
                staticKB = timeRelatedDatabase,
                libraries = defaultLibraries + timeLibrary
            ),
            goalToSolutions = slightlyMoreThan500MsGoalToSolution,
            maxDuration = 1000L
        )
    }

    /** Test with [slightlyMoreThan1100MsGoalToSolution] */
    fun testTimeout3() {
        assertSolverSolutionsCorrect(
            solver = solverOf(
                staticKB = timeRelatedDatabase,
                libraries = defaultLibraries + timeLibrary
            ),
            goalToSolutions = slightlyMoreThan1100MsGoalToSolution,
            maxDuration = 1700L
        )
    }

    /** Test with [slightlyMoreThan1800MsGoalToSolution] */
    fun testTimeout4() {
        assertSolverSolutionsCorrect(
            solver = solverOf(
                staticKB = timeRelatedDatabase,
                libraries = defaultLibraries + timeLibrary
            ),
            goalToSolutions = slightlyMoreThan1800MsGoalToSolution,
            maxDuration = 2000L
        )
    }

    /** Test with [ifThen1ToSolution] */
    fun testIfThen1(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solver = solverOf(staticKB = ifThenDatabase1),
            goalToSolutions = ifThen1ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [ifThenElse1ToSolution] */
    fun testIfThenElse1(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solver = solverOf(staticKB = ifThenDatabase1),
            goalToSolutions = ifThenElse1ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [ifThenElse2ToSolution] */
    fun testIfThenElse2(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solver = solverOf(staticKB = ifThenDatabase2),
            goalToSolutions = ifThenElse2ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [ifThen2ToSolution] */
    fun testIfThen2(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solver = solverOf(staticKB = ifThenDatabase2),
            goalToSolutions = ifThen2ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [simpleFactDatabaseNotableGoalToSolutions] */
    fun testUnification(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = simpleFactDatabase),
            simpleFactDatabaseNotableGoalToSolutions,
            maxDuration
        )
    }


    /** Test with [simpleCutDatabaseNotableGoalToSolutions] */
    fun testSimpleCutAlternatives(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = simpleCutDatabase),
            simpleCutDatabaseNotableGoalToSolutions,
            maxDuration
        )
    }

    /** Test with [simpleCutAndConjunctionDatabaseNotableGoalToSolutions] */
    fun testCutAndConjunction(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = simpleCutAndConjunctionDatabase),
            simpleCutAndConjunctionDatabaseNotableGoalToSolutions,
            maxDuration
        )
    }

    /** Test with [cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions] */
    fun testCutConjunctionAndBacktracking(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = cutConjunctionAndBacktrackingDatabase),
            cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions,
            maxDuration
        )
    }

    /** Test with [infiniteComputationDatabaseNotableGoalToSolution] */
    fun testMaxDurationParameterAndTimeOutException(maxDuration: TimeDuration = 100L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = infiniteComputationDatabase),
            infiniteComputationDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [prologStandardExampleDatabaseNotableGoalToSolution] */
    fun testPrologStandardSearchTreeExample(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = prologStandardExampleDatabase),
            prologStandardExampleDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [prologStandardExampleWithCutDatabaseNotableGoalToSolution] */
    fun testPrologStandardSearchTreeWithCutExample(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = prologStandardExampleWithCutDatabase),
            prologStandardExampleWithCutDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [customReverseListDatabaseNotableGoalToSolution] */
    fun testBacktrackingWithCustomReverseListImplementation(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = customReverseListDatabase),
            customReverseListDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [conjunctionStandardExampleDatabaseNotableGoalToSolution] */
    fun testWithPrologStandardConjunctionExamples(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = conjunctionStandardExampleDatabase),
            conjunctionStandardExampleDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties */
    fun testConjunctionProperties(maxDuration: TimeDuration = 500L) {
        prolog {
            val allDatabasesWithGoalsAndSolutions by lazy {
                allPrologTestingDatabasesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
                    listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                        ktListOf(
                            (goal and true).run { to(expectedSolutions.changeQueriesTo(this)) },
                            (true and goal).run { to(expectedSolutions.changeQueriesTo(this)) },

                            (goal and false).run {
                                when {
                                    expectedSolutions.any { it is Solution.Halt } ->
                                        to(expectedSolutions.changeQueriesTo(this))
                                    else -> hasSolutions({ no() })
                                }
                            },

                            (false and goal).hasSolutions({ no() })
                        )
                    }
                }
            }

            allDatabasesWithGoalsAndSolutions.forEach { (database, goalToSolutions) ->
                assertSolverSolutionsCorrect(
                    solverOf(staticKB = database),
                    goalToSolutions,
                    maxDuration
                )
            }
        }
    }

    /** Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleDatabaseGoalsToSolution] */
    fun testCallPrimitive(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = callStandardExampleDatabase),
            callStandardExampleDatabaseGoalsToSolution,
            maxDuration
        )

        assertSolverSolutionsCorrect(
            solverOf(),
            callTestingGoalsToSolutions,
            maxDuration
        )
    }

    /** A test in which all testing goals are called through the Call primitive */
    fun testCallPrimitiveTransparency(maxDuration: TimeDuration = 500L) {
        prolog {
            allPrologTestingDatabasesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
                listOfGoalToSolutions.map { (goal, expectedSolutions) ->
                    "call"(goal).run { to(expectedSolutions.changeQueriesTo(this)) }
                }
            }.forEach { (database, goalToSolutions) ->
                assertSolverSolutionsCorrect(
                    solverOf(staticKB = database),
                    goalToSolutions,
                    maxDuration
                )
            }
        }
    }

    /** Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowStandardExampleDatabaseNotableGoalToSolution] */
    fun testCatchPrimitive(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = catchAndThrowStandardExampleDatabase),
            catchAndThrowStandardExampleDatabaseNotableGoalToSolution,
            maxDuration
        )

        assertSolverSolutionsCorrect(
            solverOf(),
            catchTestingGoalsToSolutions,
            maxDuration
        )
    }

    /** A test in which all testing goals are called through the Catch primitive */
    fun testCatchPrimitiveTransparency(maxDuration: TimeDuration = 500L) {
        prolog {

            fun Struct.containsHaltPrimitive(): Boolean = when (functor) {
                "halt" -> true
                else -> argsSequence.filterIsInstance<Struct>().any { it.containsHaltPrimitive() }
            }

            allPrologTestingDatabasesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
                listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                    ktListOf(
                        "catch"(goal, `_`, false).run {
                            when {
                                expectedSolutions.any { it is Solution.Halt && !it.query.containsHaltPrimitive() && it.exception !is TimeOutException } ->
                                    hasSolutions({ no() })
                                else ->
                                    to(expectedSolutions.changeQueriesTo(this))
                            }
                        },
                        "catch"(goal, "notUnifyingCatcher", false).run {
                            to(expectedSolutions.changeQueriesTo(this))
                        }
                    )
                }
            }.forEach { (database, goalToSolutions) ->
                assertSolverSolutionsCorrect(
                    solverOf(staticKB = database),
                    goalToSolutions,
                    maxDuration
                )
            }
        }
    }

    /** Halt primitive testing with [haltTestingGoalsToSolutions] */
    fun testHaltPrimitive(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(),
            haltTestingGoalsToSolutions,
            maxDuration
        )
    }

    /** Not rule testing with [notStandardExampleDatabaseNotableGoalToSolution] */
    fun testNotPrimitive(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = notStandardExampleDatabase),
            notStandardExampleDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** A test in which all testing goals are called through the Not rule */
    fun testNotModularity(maxDuration: TimeDuration = 500L) {
        prolog {
            allPrologTestingDatabasesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
                listOfGoalToSolutions
                    .flatMap { (goal, expectedSolutions) ->
                        ktListOf(
                            "\\+"(goal).run {
                                when {
                                    expectedSolutions.first() is Solution.Yes -> hasSolutions({ no() })
                                    expectedSolutions.first() is Solution.No -> hasSolutions({ yes() })
                                    else -> to(expectedSolutions.changeQueriesTo(this))
                                }
                            },
                            "\\+"("\\+"(goal)).run {
                                when {
                                    expectedSolutions.first() is Solution.Yes -> hasSolutions({ yes() })
                                    expectedSolutions.first() is Solution.No -> hasSolutions({ no() })
                                    else -> to(expectedSolutions.changeQueriesTo(this))
                                }
                            }
                        )
                    }
                    .flatMap { (goal, expectedSolutions) ->
                        ktListOf(
                            goal to expectedSolutions,
                            goal.replaceAllFunctors("\\+", "not")
                                .let { it to expectedSolutions.changeQueriesTo(it) }
                        )
                    }
            }.forEach { (database, goalToSolutions) ->
                assertSolverSolutionsCorrect(
                    solverOf(staticKB = database),
                    goalToSolutions,
                    maxDuration
                )
            }
        }
    }

    /** If-Then rule testing with [ifThenStandardExampleDatabaseNotableGoalToSolution] */
    fun testIfThenRule(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = ifThenStandardExampleDatabase),
            ifThenStandardExampleDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    /** If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution] */
    fun testIfThenElseRule(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(),
            ifThenElseStandardExampleNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [customRangeListGeneratorDatabaseNotableGoalToSolution] */
    fun testNumbersRangeListGeneration(maxDuration: TimeDuration = 500L) {
        assertSolverSolutionsCorrect(
            solverOf(staticKB = customRangeListGeneratorDatabase),
            customRangeListGeneratorDatabaseNotableGoalToSolution,
            maxDuration
        )
    }

    fun testFailure(maxDuration: TimeDuration = 500L) {
        // TODO: 12/11/2019 enrich this test after solving #51
        prolog {
            val solver = solverOf()
            val query = atomOf("a")
            val solutions = solver.solve(query, maxDuration).toList()

            assertSolutionEquals(ktListOf(query.no()), solutions)
        }
    }

    fun testBasicBacktracking1(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                    { "b"(1) },
                    { "b"(2) impliedBy "!" },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(ktListOf(query.yes("N" to 2)), solutions)

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

    fun testBasicBacktracking2(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a"("X") impliedBy ("c"("X") and "b"("X")) },
                    { "b"(2) impliedBy "!" },
                    { "b"(3) },
                    { "c"(3) },
                    { "c"(2) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(
            //      with(query) { ktListOf(yes("N" to 3), yes("N" to 2)) },
            //      solutions
            // )

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

    fun testBasicBacktracking3(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a"("X") impliedBy (("b"("X") and "!") and "c"("X")) },
                    { "b"(2) },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(ktListOf(query.yes("N" to 2)), solutions)

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

    fun testBasicBacktracking4(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a"("X") impliedBy ("b"("X") and ("!" and "c"("X"))) },
                    { "b"(2) },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(ktListOf(query.yes("N" to 2)), solutions)

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

    fun testConjunction(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a" impliedBy ("b" and "c") },
                    { "b" },
                    { "c" }
                )
            )
            val query = atomOf("a")
            val solutions = solver.solve(query, maxDuration).toList()

            assertSolutionEquals(ktListOf(query.yes()), solutions)
        }
    }

    fun testConjunctionOfConjunctions(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a" impliedBy (tupleOf("b", "c") and tupleOf("d", "e")) },
                    { "b" },
                    { "c" },
                    { "d" },
                    { "e" }
                )
            )
            val query = atomOf("a")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(ktListOf(query.yes()), solutions)

            assertEquals(1, solutions.size)

            solutions[0].let {
                assertTrue { it is Solution.Yes }
                assertEquals(atomOf("a"), it.query)
                assertEquals(atomOf("a"), it.solvedQuery)
                assertTrue { it.substitution is Substitution.Unifier }
            }
        }
    }

    fun testConjunctionWithUnification(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                    { "b"(1) },
                    { "c"(1) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(ktListOf(query.yes("N" to 1)), solutions)

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

    fun testDisjunction(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a" impliedBy ("b" or "c") },
                    { "b" },
                    { "c" }
                )
            )
            val query = atomOf("a")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(
            //         with(query) { ktListOf(yes(), yes()) },
            //         solutions
            // )

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

    fun testDisjunctionWithUnification(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf(
                staticKB = theory(
                    { "a"("X") impliedBy ("b"("X") or "c"("X")) },
                    { "b"(1) },
                    { "c"(2) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(
            //        with(query) { ktListOf(yes("N" to 1), yes("N" to 2)) },
            //        solutions
            // )

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

    fun testMember(maxDuration: TimeDuration = 500L) {
        prolog {
            val solver = solverOf()

            val constants = arrayOf("a", "b", "c")
            val goal = "member"("X", listOf(*constants))

            val solutions = solver.solve(goal, maxDuration).toList()

            // TODO enable after solving #52 and remove all other assertions below
            // assertSolutionEquals(
            //        ktListOf(constants.map { goal.yes("X" to it) }, ktListOf(goal.no())).flatten(),
            //        solutions
            // )

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