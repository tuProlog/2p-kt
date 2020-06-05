package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.CustomTheories.ifThen1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThen2ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse2ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenTheory1
import it.unibo.tuprolog.solve.CustomTheories.ifThenTheory2
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
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan600MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan700MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.timeRelatedTheory
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.library.Libraries
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
                assertHasPredicateInAPI(Ground)
                assertHasPredicateInAPI(Halt)
                assertHasPredicateInAPI(Integer)
                assertHasPredicateInAPI(Is)
                assertHasPredicateInAPI(Natural)
                assertHasPredicateInAPI(NewLine)
                assertHasPredicateInAPI(NonVar)
                assertHasPredicateInAPI(NotUnifiableWith)
                assertHasPredicateInAPI(Number)
                assertHasPredicateInAPI(TermIdentical)
                assertHasPredicateInAPI(TermNotIdentical)
                assertHasPredicateInAPI(UnifiesWith)
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

            val solutions = solver.solve(query, maxDuration).toList()
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

            val solutions = solver.solve(query, maxDuration).toList()

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

            val solutions = solver.solve(query, maxDuration).toList()

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
            var solutions = solver.solve(query, maxDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("L" to listOf(1, 2, 3))),
                solutions
            )

            query = "findall"(`_`, false, "L")
            solutions = solver.solve(query, maxDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("L" to emptyList())),
                solutions
            )

            query = "findall"(`_`, "G", `_`)
            solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            maxDuration = maxDuration
        )
    }

    /** Test with [ifThenElse1ToSolution] */
    override fun testIfThenElse1() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory1),
            goalToSolutions = ifThenElse1ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [ifThenElse2ToSolution] */
    override fun testIfThenElse2() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory2),
            goalToSolutions = ifThenElse2ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [ifThen2ToSolution] */
    override fun testIfThen2() {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory2),
            goalToSolutions = ifThen2ToSolution,
            maxDuration = maxDuration
        )
    }

    /** Test with [simpleFactTheoryNotableGoalToSolutions] */
    override fun testUnification() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleFactTheory),
            simpleFactTheoryNotableGoalToSolutions,
            maxDuration
        )
    }


    /** Test with [simpleCutTheoryNotableGoalToSolutions] */
    override fun testSimpleCutAlternatives() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleCutTheory),
            simpleCutTheoryNotableGoalToSolutions,
            maxDuration
        )
    }

    /** Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions] */
    override fun testCutAndConjunction() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleCutAndConjunctionTheory),
            simpleCutAndConjunctionTheoryNotableGoalToSolutions,
            maxDuration
        )
    }

    /** Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions] */
    override fun testCutConjunctionAndBacktracking() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = cutConjunctionAndBacktrackingTheory),
            cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
            maxDuration
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
            maxDuration
        )
    }

    /** Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution] */
    override fun testPrologStandardSearchTreeWithCutExample() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.prologStandardExampleWithCutTheory),
            prologStandardExampleWithCutTheoryNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [customReverseListTheoryNotableGoalToSolution] */
    override fun testBacktrackingWithCustomReverseListImplementation() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = customReverseListTheory),
            customReverseListTheoryNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [conjunctionStandardExampleTheoryNotableGoalToSolution] */
    override fun testWithPrologStandardConjunctionExamples() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = conjunctionStandardExampleTheory),
            conjunctionStandardExampleTheoryNotableGoalToSolution,
            maxDuration
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
                    maxDuration
                )
            }
        }
    }

    /** Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution] */
    override fun testCallPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = callStandardExampleTheory),
            callStandardExampleTheoryGoalsToSolution,
            maxDuration
        )

        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            callTestingGoalsToSolutions,
            maxDuration
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
                    maxDuration
                )
            }
        }
    }

    /** Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution] */
    override fun testCatchPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = catchAndThrowTheoryExample),
            catchAndThrowTheoryExampleNotableGoalToSolution,
            maxDuration
        )

        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            catchTestingGoalsToSolutions,
            maxDuration
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
                    maxDuration
                )
            }
        }
    }

    /** Halt primitive testing with [haltTestingGoalsToSolutions] */
    override fun testHaltPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            haltTestingGoalsToSolutions,
            maxDuration
        )
    }

    /** Not rule testing with [notStandardExampleTheoryNotableGoalToSolution] */
    override fun testNotPrimitive() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = notStandardExampleTheory),
            notStandardExampleTheoryNotableGoalToSolution,
            maxDuration
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
                    maxDuration
                )
            }
        }
    }

    /** If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution] */
    override fun testIfThenRule() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = ifThenStandardExampleTheory),
            ifThenStandardExampleTheoryNotableGoalToSolution,
            maxDuration
        )
    }

    /** If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution] */
    override fun testIfThenElseRule() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            ifThenElseStandardExampleNotableGoalToSolution,
            maxDuration
        )
    }

    /** Test with [customRangeListGeneratorTheoryNotableGoalToSolution] */
    override fun testNumbersRangeListGeneration() {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = customRangeListGeneratorTheory),
            customRangeListGeneratorTheoryNotableGoalToSolution,
            maxDuration
        )
    }

    override fun testFailure() {
        // TODO: 12/11/2019 enrich this test after solving #51
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atomOf("a")
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

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
            val solutions = solver.solve(query, maxDuration).toList()

            assertSolutionEquals(
                with(query) { ktListOf(yes("N" to 1), yes("N" to 2)) },
                solutions
            )

            assertEquals(2, solutions.size)
        }
    }

    private fun <T> ktListConcat(l1: List<T>, l2: List<T>): List<T> = l1 + l2

    override fun testMember() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val constants = ktListOf("a", "b", "c")
            val goal = "member"("X", constants.toTerm())

            val solutions = solver.solve(goal, maxDuration).toList()

            assertSolutionEquals(
                ktListConcat(
                    constants.map { goal.yes("X" to it) },
                    ktListOf(goal.no())
                ),
                solutions
            )

            assertEquals(constants.size + 1, solutions.size)
        }
    }
}