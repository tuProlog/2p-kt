package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.CustomTheories.ifThen1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThen2ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse2ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenTheory1
import it.unibo.tuprolog.solve.CustomTheories.ifThenTheory2
import it.unibo.tuprolog.solve.CustomTheories.memberGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.callStandardExampleTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.callStandardExampleTheoryGoalsToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.catchAndThrowTheoryExample
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.catchAndThrowTheoryExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.conjunctionStandardExampleTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.conjunctionStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenElseStandardExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenStandardExampleTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.notStandardExampleTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.notStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleWithCutTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.allPrologTestingTheoriesToRespectiveGoalsAndSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.callTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.catchTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.customRangeListGeneratorTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.customRangeListGeneratorTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.customReverseListTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.customReverseListTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.cutConjunctionAndBacktrackingTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.haltTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.infiniteComputationTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.infiniteComputationTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.replaceAllFunctors
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TimeRelatedTheories.lessThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan600MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan700MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.timeRelatedTheory
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.stdlib.primitive.*
import it.unibo.tuprolog.solve.stdlib.rule.Arrow
import it.unibo.tuprolog.solve.stdlib.rule.Member
import it.unibo.tuprolog.solve.stdlib.rule.Not
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import it.unibo.tuprolog.solve.stdlib.primitive.Float as FloatPrimitive
import kotlin.collections.listOf as ktListOf

internal class SolverTestImpl(private val solverFactory: SolverFactory) : SolverTest {

    /** Test presence of correct built-ins */
    override fun testBuiltinApi() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            with(solver) {
                assertHasPredicateInAPI("!", 0)
                assertHasPredicateInAPI("call", 1)
                assertHasPredicateInAPI("catch", 3)
                assertHasPredicateInAPI("throw", 1)
                assertHasPredicateInAPI(",", 2)
                assertHasPredicateInAPI("\\+", 1)
                assertHasPredicateInAPI(Arrow)
                assertHasPredicateInAPI(Member.SIGNATURE)
                assertHasPredicateInAPI(Not)
                assertHasPredicateInAPI(Semicolon.SIGNATURE)
                assertHasPredicateInAPI(ArithmeticEqual)
                assertHasPredicateInAPI(ArithmeticGreaterThan)
                assertHasPredicateInAPI(ArithmeticGreaterThanOrEqualTo)
                assertHasPredicateInAPI(ArithmeticLowerThan)
                assertHasPredicateInAPI(ArithmeticLowerThanOrEqualTo)
                assertHasPredicateInAPI(ArithmeticNotEqual)
                assertHasPredicateInAPI(Assert)
                assertHasPredicateInAPI(AssertA)
                assertHasPredicateInAPI(AssertZ)
                assertHasPredicateInAPI(Atom)
                assertHasPredicateInAPI(Atomic)
                assertHasPredicateInAPI(Callable)
                assertHasPredicateInAPI(Compound)
                assertHasPredicateInAPI(EnsureExecutable)
                assertHasPredicateInAPI(FloatPrimitive)
                assertHasPredicateInAPI(Functor)
                assertHasPredicateInAPI(Ground)
                assertHasPredicateInAPI(Halt)
                assertHasPredicateInAPI(Integer)
                assertHasPredicateInAPI(Is)
                assertHasPredicateInAPI(Natural)
                assertHasPredicateInAPI(NewLine)
                assertHasPredicateInAPI(NonVar)
                assertHasPredicateInAPI(NotUnifiableWith)
                assertHasPredicateInAPI(Number)
                assertHasPredicateInAPI(Retract)
                assertHasPredicateInAPI(Sleep)
                assertHasPredicateInAPI(TermIdentical)
                assertHasPredicateInAPI(TermNotIdentical)
                assertHasPredicateInAPI(UnifiesWith)
                assertHasPredicateInAPI(Univ)
                assertHasPredicateInAPI(Var)
                assertHasPredicateInAPI(Write)
            }
        }
    }

    private fun testAssert(suffix: String, inverse: Boolean) {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val assert = "assert$suffix"

            val query = assert("f"(1)) and
                    assert("f"(2)) and
                    assert("f"(3)) and
                    "f"("X")

            val solutions = solver.solve(query, mediumDuration).toList()
            val ints = if (inverse) (3 downTo 1) else (1..3)

            assertSolutionEquals(
                (ints).map { query.yes("X" to it) },
                solutions
            )

            ints.forEach {
                assertTrue {
                    "f"(it) in solver.dynamicKb
                }
            }
        }
    }

    override fun testAssert() {
        testAssert("", false)
    }

    override fun testAssertZ() {
        testAssert("z", false)
    }

    override fun testAssertA() {
        testAssert("a", true)
    }

    override fun testWrite() {
        val outputs = mutableListOf<String>()
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertNotNull(solver.standardOutput)

            with(solver.standardOutput!!) {
                addListener { outputs += it }
            }

            val query = "write"(atomOf("atom")) and
                    "write"(atomOf("a string")) and
                    "write"(varOf("A_Var")) and
                    "write"(numOf(1)) and
                    "write"(numOf(2.1)) and
                    "write"("f"("x")) and
                    "nl"

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            assertEquals(
                ktListOf(
                    "atom",
                    "a string",
                    varOf("A_Var").completeName,
                    "1",
                    "2.1",
                    "f(x)",
                    "\n"
                ),
                outputs
            )
        }
    }

    override fun testStandardOutput() {
        val outputs = mutableListOf<String>()
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertNotNull(solver.standardOutput)

            with(solver.standardOutput!!) {
                addListener { outputs += it }
                write("a")
            }

            val query = "write"("b") and "write"("c") and "write"("d") and "nl"

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            solver.standardOutput?.write("e")

            assertEquals(
                ktListOf("a", "b", "c", "d", "\n", "e"),
                outputs
            )
        }
    }

    override fun testFindAll() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theoryOf(
                    fact { "a"(1) },
                    fact { "a"(2) },
                    fact { "a"(3) }
                )
            )

            var query = "findall"("N", "a"("N"), "L")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("L" to listOf(1, 2, 3))),
                solutions
            )

            query = "findall"(`_`, false, "L")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("L" to emptyList())),
                solutions
            )

            query = "findall"(`_`, "G", `_`)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forGoal(
                            DummyInstances.executionContext,
                            Signature("findall", 3),
                            varOf("G")
                        )
                    )
                ),
                solutions
            )
        }
    }

    /** Test `true` goal */
    override fun testTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = truthOf(true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    /** Test with [lessThan500MsGoalToSolution] */
    override fun testTimeout1() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = timeRelatedTheory
            ),
            goalToSolutions = lessThan500MsGoalToSolution,
            maxDuration = 400L
        )
    }

    /** Test with [slightlyMoreThan500MsGoalToSolution] */
    override fun testTimeout2() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = timeRelatedTheory
            ),
            goalToSolutions = slightlyMoreThan500MsGoalToSolution,
            maxDuration = 599L
        )
    }

    /** Test with [slightlyMoreThan600MsGoalToSolution] */
    override fun testTimeout3() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = timeRelatedTheory
            ),
            goalToSolutions = slightlyMoreThan600MsGoalToSolution,
            maxDuration = 699L
        )
    }

    /** Test with [slightlyMoreThan700MsGoalToSolution] */
    override fun testTimeout4() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = timeRelatedTheory
            ),
            goalToSolutions = slightlyMoreThan700MsGoalToSolution,
            maxDuration = 799L
        )
    }

    /** Test with [ifThen1ToSolution] */
    override fun testIfThen1() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory1),
            goalToSolutions = ifThen1ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [ifThenElse1ToSolution] */
    override fun testIfThenElse1() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory1),
            goalToSolutions = ifThenElse1ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [ifThenElse2ToSolution] */
    override fun testIfThenElse2() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory2),
            goalToSolutions = ifThenElse2ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [ifThen2ToSolution] */
    override fun testIfThen2() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory2),
            goalToSolutions = ifThen2ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [simpleFactTheoryNotableGoalToSolutions] */
    override fun testUnification() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleFactTheory),
            simpleFactTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }


    /** Test with [simpleCutTheoryNotableGoalToSolutions] */
    override fun testSimpleCutAlternatives() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleCutTheory),
            simpleCutTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions] */
    override fun testCutAndConjunction() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleCutAndConjunctionTheory),
            simpleCutAndConjunctionTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions] */
    override fun testCutConjunctionAndBacktracking() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = cutConjunctionAndBacktrackingTheory),
            cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [infiniteComputationTheoryNotableGoalToSolution] */
    override fun testMaxDurationParameterAndTimeOutException() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = infiniteComputationTheory),
            infiniteComputationTheoryNotableGoalToSolution,
            shortDuration
        )
    }

    /** Test with [prologStandardExampleTheoryNotableGoalToSolution] */
    override fun testPrologStandardSearchTreeExample() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = prologStandardExampleTheory),
            prologStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution] */
    override fun testPrologStandardSearchTreeWithCutExample() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.prologStandardExampleWithCutTheory),
            prologStandardExampleWithCutTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [customReverseListTheoryNotableGoalToSolution] */
    override fun testBacktrackingWithCustomReverseListImplementation() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = customReverseListTheory),
            customReverseListTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [conjunctionStandardExampleTheoryNotableGoalToSolution] */
    override fun testWithPrologStandardConjunctionExamples() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = conjunctionStandardExampleTheory),
            conjunctionStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties */
    override fun testConjunctionProperties() {
        prolog {
            val allDatabasesWithGoalsAndSolutions by lazy {
                allPrologTestingTheoriesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
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
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration
                )
            }
        }
    }

    /** Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution] */
    override fun testCallPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = callStandardExampleTheory),
            callStandardExampleTheoryGoalsToSolution,
            mediumDuration
        )

        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            callTestingGoalsToSolutions,
            mediumDuration
        )
    }

    /** A test in which all testing goals are called through the Call primitive */
    override fun testCallPrimitiveTransparency() {
        prolog {
            allPrologTestingTheoriesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
                listOfGoalToSolutions.map { (goal, expectedSolutions) ->
                    "call"(goal).run { to(expectedSolutions.changeQueriesTo(this)) }
                }
            }.forEach { (database, goalToSolutions) ->
                assertSolverSolutionsCorrect(
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration
                )
            }
        }
    }

    /** Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution] */
    override fun testCatchPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = catchAndThrowTheoryExample),
            catchAndThrowTheoryExampleNotableGoalToSolution,
            mediumDuration
        )

        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            catchTestingGoalsToSolutions,
            mediumDuration
        )
    }

    /** A test in which all testing goals are called through the Catch primitive */
    override fun testCatchPrimitiveTransparency() {
        prolog {

            fun Struct.containsHaltPrimitive(): Boolean = when (functor) {
                "halt" -> true
                else -> argsSequence.filterIsInstance<Struct>()
                    .any { it.containsHaltPrimitive() }
            }

            allPrologTestingTheoriesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
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
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration
                )
            }
        }
    }

    /** Halt primitive testing with [haltTestingGoalsToSolutions] */
    override fun testHaltPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            haltTestingGoalsToSolutions,
            mediumDuration
        )
    }

    /** Not rule testing with [notStandardExampleTheoryNotableGoalToSolution] */
    override fun testNotPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = notStandardExampleTheory),
            notStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** A test in which all testing goals are called through the Not rule */
    override fun testNotModularity() {
        prolog {
            allPrologTestingTheoriesToRespectiveGoalsAndSolutions.mapValues { (_, listOfGoalToSolutions) ->
                listOfGoalToSolutions
                    .flatMap { (goal, expectedSolutions) ->
                        ktListOf(
                            "\\+"(goal).run {
                                when {
                                    expectedSolutions.first() is Solution.Yes -> hasSolutions(
                                        { no() })
                                    expectedSolutions.first() is Solution.No -> hasSolutions(
                                        { yes() })
                                    else -> to(expectedSolutions.changeQueriesTo(this))
                                }
                            },
                            "\\+"("\\+"(goal)).run {
                                when {
                                    expectedSolutions.first() is Solution.Yes -> hasSolutions(
                                        { yes() })
                                    expectedSolutions.first() is Solution.No -> hasSolutions(
                                        { no() })
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
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration
                )
            }
        }
    }

    /** If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution] */
    override fun testIfThenRule() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = ifThenStandardExampleTheory),
            ifThenStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution] */
    override fun testIfThenElseRule() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            ifThenElseStandardExampleNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [customRangeListGeneratorTheoryNotableGoalToSolution] */
    override fun testNumbersRangeListGeneration() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = customRangeListGeneratorTheory),
            customRangeListGeneratorTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    override fun testFailure() {
        // TODO: 12/11/2019 enrich this test after solving #51
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.no()
                ), solutions
            )
        }
    }

    override fun testBasicBacktracking1() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                    { "b"(1) },
                    { "b"(2) impliedBy "!" },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes("N" to 2)
                ), solutions
            )
        }
    }

    override fun testBasicBacktracking2() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"("X") impliedBy ("c"("X") and "b"("X")) },
                    { "b"(2) impliedBy "!" },
                    { "b"(3) },
                    { "c"(3) },
                    { "c"(2) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { ktListOf(yes("N" to 3), yes("N" to 2)) },
                solutions
            )
        }
    }

    override fun testBasicBacktracking3() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"("X") impliedBy (("b"("X") and "!") and "c"("X")) },
                    { "b"(2) },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes("N" to 2)
                ), solutions
            )
        }
    }

    override fun testBasicBacktracking4() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"("X") impliedBy ("b"("X") and ("!" and "c"("X"))) },
                    { "b"(2) },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes("N" to 2)
                ), solutions
            )
        }
    }

    override fun testConjunction() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a" impliedBy ("b" and "c") },
                    { "b" },
                    { "c" }
                )
            )
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes()
                ), solutions
            )
        }
    }

    override fun testConjunctionOfConjunctions() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a" impliedBy (tupleOf("b", "c") and tupleOf("d", "e")) },
                    { "b" },
                    { "c" },
                    { "d" },
                    { "e" }
                )
            )
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes()
                ), solutions
            )
        }
    }

    override fun testConjunctionWithUnification() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"("X") impliedBy ("b"("X") and "c"("X")) },
                    { "b"(1) },
                    { "c"(1) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes("N" to 1)
                ), solutions
            )
        }
    }

    override fun testDisjunction() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a" impliedBy ("b" or "c") },
                    { "b" },
                    { "c" }
                )
            )
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { ktListOf(yes(), yes()) },
                solutions
            )
        }
    }

    override fun testDisjunctionWithUnification() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"("X") impliedBy ("b"("X") or "c"("X")) },
                    { "b"(1) },
                    { "c"(2) }
                )
            )
            val query = "a"("N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { ktListOf(yes("N" to 1), yes("N" to 2)) },
                solutions
            )

            assertEquals(2, solutions.size)
        }
    }

    override fun testMember() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertSolverSolutionsCorrect(solver, memberGoalToSolution, mediumDuration)
        }
    }

    override fun testAssertRules() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "assertz"("f"(2) impliedBy false) and "asserta"("f"(1) impliedBy true)

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            assertEquals(
                ktListOf(
                    factOf(structOf("f", numOf(1))),
                    ruleOf(structOf("f", numOf(2)), atomOf("false"))
                ),
                solver.dynamicKb.toList()
            )
        }
    }

    override fun testRetract() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                dynamicKb = theoryOf(
                    factOf(structOf("f", numOf(1))),
                    ruleOf(structOf("f", numOf(2)), atomOf("false"))
                )
            )

            val query = "retract"("f"("X"))

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes("X" to 1),
                    query.yes("X" to 2),
                    query.no()

                ),
                solutions
            )

            assertEquals(
                ktListOf(),
                solver.dynamicKb.toList()
            )
            assertEquals(0L, solver.dynamicKb.size)
        }
    }

    override fun testNatural() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "natural"("X") and "natural"("X")

            val n = 100

            val solutions = solver.solve(query, mediumDuration).take(n).toList()

            assertSolutionEquals(
                (0 until n).map { query.yes("X" to it) },
                solutions
            )
        }
    }

    override fun testFunctor() {

        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "functor"("a"("b", "c"), "X", "Y")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to "a", "Y" to 2)),
                solutions
            )

            query = "functor"("a"("b", "c"), "a", "Y")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("Y" to 2)),
                solutions
            )

            query = "functor"("a"("b", "c"), "X", 2)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to "a")),
                solutions
            )

            query = "functor"("X", "a", 2)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to structOf("a", anonymous(), anonymous()))),
                solutions
            )

            query = "functor"("X", "Y", 2)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.halt(InstantiationError.forArgument(
                    DummyInstances.executionContext,
                    Signature("functor", 3),
                    1,
                    varOf("Y")))),
                solutions
            )

            query = "functor"("X", "a", "2")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.halt(TypeError.forArgument(
                    DummyInstances.executionContext,
                    Signature("functor", 3),
                    TypeError.Expected.INTEGER,
                    varOf("Y"),
                    2))),
                solutions
            )
        }
    }

    override fun testUniv() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = "a"("b", "c") univ "X"
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to listOf("a","b","c"))),
                solutions
            )

            query = "X" univ listOf("a","b","c")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to structOf("a","b","c"))),
                solutions
            )

            query = "X" univ "Y"
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.halt(InstantiationError.forArgument(
                    DummyInstances.executionContext,
                    Signature("=..", 2),
                    0,
                    varOf("X")))),
                solutions
            )

            query = "a"("b", "c") univ "a"
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.halt(TypeError.forArgument(
                    DummyInstances.executionContext,
                    Signature("=..", 2),
                    TypeError.Expected.LIST,
                    atomOf("a"),
                    1))),
                solutions
            )
        }
    }
}