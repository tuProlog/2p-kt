@file:Suppress("LongMethod", "LargeClass", "CyclomaticComplexMethod")

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.CustomTheories
import it.unibo.tuprolog.solve.CustomTheories.ifThen1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThen2ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse1ToSolution
import it.unibo.tuprolog.solve.CustomTheories.ifThenElse2ToSolution
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.PrologStandardExampleTheories
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.callStandardExampleTheoryGoalsToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.catchAndThrowTheoryExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.conjunctionStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenElseStandardExampleNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.ifThenStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.notStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleWithCutTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TestingClauseTheories
import it.unibo.tuprolog.solve.TestingClauseTheories.callTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.catchTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.customRangeListGeneratorTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.customReverseListTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.haltTestingGoalsToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.infiniteComputationTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingTermOperators
import it.unibo.tuprolog.solve.TimeRelatedTheories
import it.unibo.tuprolog.solve.TimeRelatedTheories.lessThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan600MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan700MsGoalToSolution
import it.unibo.tuprolog.solve.assertHasPredicateInAPI
import it.unibo.tuprolog.solve.changeQueriesTo
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.warning.MissingPredicate
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.hasSolutions
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.collections.plus as append

fun concurrentSolverContract() =
    concurrentContract(
        disabled =
            setOf(
                "solver/side-effects-persistent-after-backtracking1", // Side effects need to be implemented
                "solver/if-then1", // Cut need to be implemented
                "solver/if-then2", // Cut need to be implemented
                "solver/if-then-else1", // Cut need to be implemented
                "solver/if-then-else2", // Cut need to be implemented
                "solver/timeout1", // unhandled sleep
                "solver/timeout2", // unhandled sleep
                "solver/timeout3", // unhandled sleep
                "solver/timeout4", // unhandled sleep
                "solver/simple-cut-alternatives", // Cut need to be implemented
                "solver/cut-and-conjunction", // Cut need to be implemented
                "solver/cut-conjunction-and-backtracking", // Cut need to be implemented
                // timeout need to be implemented, does not terminate
                "solver/max-duration-parameter-and-time-out-exception",
                "solver/prolog-standard-search-tree-example", // if is nondeterministic
                "solver/prolog-standard-search-tree-with-cut-example", // Cut need to be implemented
                "solver/backtracking-with-custom-reverse-list-implementation", // Cut need to be implemented
                "solver/conjunction-properties", // Cut need to be implemented
                "solver/call-primitive", // Cut need to be implemented
                "solver/call-primitive-transparency", // Cut need to be implemented
                "solver/catch-primitive", // same solution count does not match (probably correct)
                "solver/catch-primitive-transparency", // solutions do not match (probably correct)
                "solver/halt-primitive", // todo check needed, solutions appear equals but tests fail
                "solver/not-primitive", // todo check needed, solution is not halt but unknown cause
                "solver/not-modularity", // todo check needed, solutions do not match
                "solver/if-then-rule", // Arrow need to be implemented
                "solver/if-then-else-rule", // Arrow need to be implemented
                "solver/builtin-api", // some API need to be implemented (abolish, assert, etc.)
                "solver/basic-backtracking1", // backtracking is not present in concurrent solver, probably useless test
                "solver/basic-backtracking2", // backtracking is not present in concurrent solver, probably useless test
                "solver/basic-backtracking3", // backtracking is not present in concurrent solver, probably useless test
                "solver/basic-backtracking4", // backtracking is not present in concurrent solver, probably useless test
                "solver/retract", // Retract need to be implemented
                "solver/natural", // todo check needed, more solutions than expected
                "solver/retract-all", // Retract need to be implemented
                "solver/append", // Append need to be implemented
            ),
    ) {
        scenario("solver/unknown-flag2") {
            logicProgramming {
                val theory =
                    theoryOf(
                        rule {
                            "ancestor"(X, Y) `if` "parent"(X, Y)
                        },
                        rule {
                            "ancestor"(X, Y) `if` ("parent"(X, Z) and "ancestor"(Z, Y))
                        },
                        fact { "parent"("abraham", "isaac") },
                        fact { "parent"("isaac", "jacob") },
                        fact { "parent"("jacob", "joseph") },
                    )

                val observedWarnings = mutableListOf<Warning>()

                var solver =
                    solverWithDefaultBuiltins(
                        staticKb = theory,
                        flags = FlagStore.of(Unknown to Unknown.WARNING),
                        warnings =
                            OutputChannel.of {
                                observedWarnings.add(it)
                            },
                    )

                val query = "ancestor"("abraham", X)

                var solutions = fromSequence(solver.solve(query))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes(X to "isaac"),
                            query.yes(X to "jacob"),
                            query.yes(X to "joseph"),
                            query.no(),
                        ),
                    )

                expected.assertingEquals(solutions)
                assertEquals(mutableListOf(), observedWarnings)

                solver =
                    solverWithDefaultBuiltins(
                        staticKb = theory,
                        flags = FlagStore.of(Unknown to Unknown.ERROR),
                        warnings =
                            OutputChannel.of {
                                observedWarnings.add(it)
                            },
                    )

                solutions = fromSequence(solver.solve(query))

                expected.assertingEquals(solutions)
                assertEquals(mutableListOf(), observedWarnings)

                solver =
                    solverWithDefaultBuiltins(
                        staticKb = theory,
                        flags = FlagStore.of(Unknown to Unknown.FAIL),
                        warnings =
                            OutputChannel.of {
                                observedWarnings.add(it)
                            },
                    )
                solutions = fromSequence(solver.solve(query))

                expected.assertingEquals(solutions)
                assertEquals(mutableListOf(), observedWarnings)
            }
        }

        scenario("solver/unknown-flag1") {
            logicProgramming {
                val query = "missing_predicate"(X)

                val observedWarnings = mutableListOf<Warning>()

                var solver =
                    solverWithDefaultBuiltins(
                        flags = FlagStore.of(Unknown to Unknown.ERROR),
                        warnings =
                            OutputChannel.of {
                                observedWarnings.add(it)
                            },
                    )

                var solutions = fromSequence(solver.solve(query))
                var expected =
                    fromSequence(
                        query.halt(
                            ExistenceError.forProcedure(
                                DummyInstances.executionContext,
                                query.extractSignature(),
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
                assertTrue { observedWarnings.isEmpty() }

                solver =
                    solverWithDefaultBuiltins(
                        flags = FlagStore.of(Unknown to Unknown.WARNING),
                        warnings =
                            OutputChannel.of {
                                observedWarnings.add(it)
                            },
                    )

                solutions = fromSequence(solver.solve(query))
                expected = fromSequence(query.no())

                expected.assertingEquals(solutions)
                assertEquals(1, observedWarnings.size)
                assertTrue { observedWarnings[0] is MissingPredicate }
                assertEquals(query.extractSignature(), (observedWarnings[0] as MissingPredicate).signature)

                solver =
                    solverWithDefaultBuiltins(
                        flags = FlagStore.of(Unknown to Unknown.FAIL),
                        warnings =
                            OutputChannel.of {
                                observedWarnings.add(it)
                            },
                    )

                solutions = fromSequence(solver.solve(query))
                expected = fromSequence(query.no())

                expected.assertingEquals(solutions)
                assertTrue { observedWarnings.size == 1 }
            }
        }

        scenario("solver/side-effects-persistent-after-backtracking1") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        dynamicKb =
                            theoryOf(
                                fact { "f"(1) },
                                fact { "f"(2) },
                                fact { "f"(3) },
                            ),
                        staticKb =
                            theoryOf(
                                clause { "getf"(F) `if` findall(X, "f"(X), F) },
                                clause { "getg"(G) `if` findall(X, "g"(X), G) },
                                clause {
                                    "ftog"(F, G) `if` (
                                        retract("f"(X)) and
                                            assert("g"(X)) and
                                            "getf"(F) and
                                            "getg"(G)
                                    )
                                },
                            ),
                    )

                val query = "ftog"(X, Y)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes(X to logicListOf(2, 3), Y to logicListOf(1)),
                            query.yes(X to logicListOf(3), Y to logicListOf(1, 2)),
                            query.yes(X to emptyLogicList, Y to logicListOf(1, 2, 3)),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/find-all") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theoryOf(
                                fact { "a"(1) },
                                fact { "a"(2) },
                                fact { "a"(3) },
                            ),
                    )

                var query = findall(N, "a"(N), L)

                var solutions = fromSequence(solver.solve(query, mediumDuration))
                var expected = fromSequence(query.yes(L to logicListOf(1, 2, 3)))

                expected.assertingEquals(solutions)

                query = findall(`_`, false, L)
                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(L to emptyLogicList))

                expected.assertingEquals(solutions)

                query = findall(`_`, G, `_`)
                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("findall", 3),
                                variable = G,
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/assert") {
            testAssert("", false)
        }

        scenario("solver/assert-z") {
            testAssert("z", false)
        }

        scenario("solver/assert-a") {
            testAssert("a", true)
        }

        scenario("solver/write") {
            val outputs = mutableListOf<String>()
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                solver.standardOutput.addListener { outputs += it!! }

                val terms =
                    listOf(
                        atomOf("atom"),
                        atomOf("a string"),
                        varOf("A_Var"),
                        numOf(1),
                        numOf(2.1),
                        "f"("x"),
                    )

                val query = tupleOf(terms.map { write(it) }.append(nl))

                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)

                assertEquals(
                    terms.map { it.format(TermFormatter.default()) }.append("\n"),
                    outputs,
                )
            }
        }

        scenario("solver/standard-output") {
            val outputs = mutableListOf<String>()
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                with(solver.standardOutput) {
                    addListener { outputs += it!! }
                    write("a")
                }

                val query = write("b") and write("c") and write("d") and nl

                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)

                solver.standardOutput.write("e")

                assertEquals(
                    listOf("a", "b", "c", "d", "\n", "e"),
                    outputs,
                )
            }
        }

        // Test `true` goal

        case("solver/true") { truthOf(true) yields { listOf(it.yes()) } }

        // Test with [ifThen1ToSolution]

        scenario("solver/if-then1") {
            assertConcurrentSolverSolutionsCorrect(
                solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory1),
                goalToSolutions = ifThen1ToSolution,
                maxDuration = mediumDuration,
            )
        }

        // Test with [ifThen2ToSolution]

        scenario("solver/if-then2") {
            assertConcurrentSolverSolutionsCorrect(
                solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory2),
                goalToSolutions = ifThen2ToSolution,
                maxDuration = mediumDuration,
            )
        }

        // Test with [ifThenElse1ToSolution]

        scenario("solver/if-then-else1") {
            assertConcurrentSolverSolutionsCorrect(
                solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory1),
                goalToSolutions = ifThenElse1ToSolution,
                maxDuration = mediumDuration,
            )
        }

        // Test with [ifThenElse2ToSolution]

        scenario("solver/if-then-else2") {
            assertConcurrentSolverSolutionsCorrect(
                solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory2),
                goalToSolutions = ifThenElse2ToSolution,
                maxDuration = mediumDuration,
            )
        }

        // Test with [lessThan500MsGoalToSolution]

        scenario("solver/timeout1") {
            assertConcurrentSolverSolutionsCorrect(
                solver =
                    solverWithDefaultBuiltins(
                        staticKb = TimeRelatedTheories.timeRelatedTheory,
                    ),
                goalToSolutions = lessThan500MsGoalToSolution,
                maxDuration = 400L,
            )
        }

        // Test with [slightlyMoreThan500MsGoalToSolution]

        scenario("solver/timeout2") {
            assertConcurrentSolverSolutionsCorrect(
                solver =
                    solverWithDefaultBuiltins(
                        staticKb = TimeRelatedTheories.timeRelatedTheory,
                    ),
                goalToSolutions = slightlyMoreThan500MsGoalToSolution,
                maxDuration = 599L,
            )
        }

        // Test with [slightlyMoreThan600MsGoalToSolution]

        scenario("solver/timeout3") {
            assertConcurrentSolverSolutionsCorrect(
                solver =
                    solverWithDefaultBuiltins(
                        staticKb = TimeRelatedTheories.timeRelatedTheory,
                    ),
                goalToSolutions = slightlyMoreThan600MsGoalToSolution,
                maxDuration = 699L,
            )
        }

        // Test with [slightlyMoreThan700MsGoalToSolution]

        scenario("solver/timeout4") {
            assertConcurrentSolverSolutionsCorrect(
                solver =
                    solverWithDefaultBuiltins(
                        staticKb = TimeRelatedTheories.timeRelatedTheory,
                    ),
                goalToSolutions = slightlyMoreThan700MsGoalToSolution,
                maxDuration = 799L,
            )
        }

        // Test with [simpleFactTheoryNotableGoalToSolutions]

        scenario("solver/unification") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.simpleFactTheory),
                simpleFactTheoryNotableGoalToSolutions,
                mediumDuration,
            )
        }

        // Test with [simpleCutTheoryNotableGoalToSolutions]

        scenario("solver/simple-cut-alternatives") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.simpleCutTheory),
                simpleCutTheoryNotableGoalToSolutions,
                mediumDuration,
            )
        }

        // Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions]

        scenario("solver/cut-and-conjunction") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.simpleCutAndConjunctionTheory),
                simpleCutAndConjunctionTheoryNotableGoalToSolutions,
                mediumDuration,
            )
        }

        // Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions]

        scenario("solver/cut-conjunction-and-backtracking") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.cutConjunctionAndBacktrackingTheory),
                cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
                mediumDuration,
            )
        }

        // Test with [infiniteComputationTheoryNotableGoalToSolution]

        scenario("solver/max-duration-parameter-and-time-out-exception") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.infiniteComputationTheory),
                infiniteComputationTheoryNotableGoalToSolution,
                shortDuration,
            )
        }

        // Test with [prologStandardExampleTheoryNotableGoalToSolution]

        scenario("solver/prolog-standard-search-tree-example") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.prologStandardExampleTheory),
                prologStandardExampleTheoryNotableGoalToSolution,
                mediumDuration,
            )
        }

        // Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution]

        scenario("solver/prolog-standard-search-tree-with-cut-example") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.prologStandardExampleWithCutTheory),
                prologStandardExampleWithCutTheoryNotableGoalToSolution,
                mediumDuration,
            )
        }

        // Test with [customReverseListTheoryNotableGoalToSolution]

        scenario("solver/backtracking-with-custom-reverse-list-implementation") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.customReverseListTheory),
                customReverseListTheoryNotableGoalToSolution,
                mediumDuration,
            )
        }

        // Test with [conjunctionStandardExampleTheoryNotableGoalToSolution]

        scenario("solver/with-prolog-standard-conjunction-examples") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.conjunctionStandardExampleTheory),
                conjunctionStandardExampleTheoryNotableGoalToSolution,
                mediumDuration,
            )
        }

        // A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties

        scenario("solver/conjunction-properties") {
            logicProgramming {
                val allDatabasesWithGoalsAndSolutions by lazy {
                    TestingClauseTheories
                        .allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                            callErrorSignature,
                            nafErrorSignature,
                            notErrorSignature,
                        ).mapValues { (_, listOfGoalToSolutions) ->
                            listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                                listOf(
                                    (goal and true).run { to(expectedSolutions.changeQueriesTo(this)) },
                                    (true and goal).run { to(expectedSolutions.changeQueriesTo(this)) },
                                    (goal and false).run {
                                        when {
                                            expectedSolutions.any { it is Solution.Halt } ->
                                                to(expectedSolutions.changeQueriesTo(this))
                                            else -> hasSolutions({ no() })
                                        }
                                    },
                                    (false and goal).hasSolutions({ no() }),
                                )
                            }
                        }
                }

                allDatabasesWithGoalsAndSolutions.forEach { (database, goalToSolutions) ->
                    assertConcurrentSolverSolutionsCorrect(
                        solverWithDefaultBuiltins(staticKb = database),
                        goalToSolutions,
                        mediumDuration,
                    )
                }
            }
        }

        // Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution]

        scenario("solver/call-primitive") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.callStandardExampleTheory),
                callStandardExampleTheoryGoalsToSolution(callErrorSignature),
                mediumDuration,
            )

            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                callTestingGoalsToSolutions(callErrorSignature),
                mediumDuration,
            )
        }

        // A test in which all testing goals are called through the Call primitive

        scenario("solver/call-primitive-transparency") {
            logicProgramming {
                TestingClauseTheories
                    .allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                        callErrorSignature,
                        nafErrorSignature,
                        notErrorSignature,
                    ).mapValues { (_, listOfGoalToSolutions) ->
                        listOfGoalToSolutions.map { (goal, expectedSolutions) ->
                            call(goal).run { to(expectedSolutions.changeQueriesTo(this)) }
                        }
                    }.forEach { (database, goalToSolutions) ->
                        assertConcurrentSolverSolutionsCorrect(
                            solverWithDefaultBuiltins(staticKb = database),
                            goalToSolutions,
                            mediumDuration,
                        )
                    }
            }
        }

        // Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution]

        // Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution]

        scenario("solver/catch-primitive") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.catchAndThrowTheoryExample),
                catchAndThrowTheoryExampleNotableGoalToSolution,
                mediumDuration,
            )

            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                catchTestingGoalsToSolutions,
                mediumDuration,
            )
        }

        // A test in which all testing goals are called through the Catch primitive

        scenario("solver/catch-primitive-transparency") {
            logicProgramming {
                fun Struct.containsHaltPrimitive(): Boolean =
                    when (functor) {
                        "halt" -> true
                        else -> argsSequence.filterIsInstance<Struct>().any { it.containsHaltPrimitive() }
                    }

                TestingClauseTheories
                    .allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                        callErrorSignature,
                        nafErrorSignature,
                        notErrorSignature,
                    ).mapValues { (_, listOfGoalToSolutions) ->
                        listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                            listOf(
                                `catch`(goal, `_`, false).run {
                                    when {
                                        expectedSolutions.any {
                                            it is Solution.Halt &&
                                                !it.query.containsHaltPrimitive() &&
                                                it.exception !is TimeOutException
                                        } -> hasSolutions({ no() })
                                        else -> to(expectedSolutions.changeQueriesTo(this))
                                    }
                                },
                                `catch`(goal, "notUnifyingCatcher", false).run {
                                    to(expectedSolutions.changeQueriesTo(this))
                                },
                            )
                        }
                    }.forEach { (database, goalToSolutions) ->
                        assertConcurrentSolverSolutionsCorrect(
                            solverWithDefaultBuiltins(staticKb = database),
                            goalToSolutions,
                            mediumDuration,
                        )
                    }
            }
        }

        // Halt primitive testing with [haltTestingGoalsToSolutions]

        scenario("solver/halt-primitive") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                haltTestingGoalsToSolutions,
                mediumDuration,
            )
        }

        // Not rule testing with [notStandardExampleTheoryNotableGoalToSolution]

        scenario("solver/not-primitive") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.notStandardExampleTheory),
                notStandardExampleTheoryNotableGoalToSolution(nafErrorSignature, notErrorSignature),
                mediumDuration,
            )
        }

        // A test in which all testing goals are called through the Not rule

        scenario("solver/not-modularity") {
            logicProgramming {
                TestingClauseTheories
                    .allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                        callErrorSignature,
                        nafErrorSignature,
                        notErrorSignature,
                    ).mapValues { (_, listOfGoalToSolutions) ->
                        listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                            listOf(
                                naf(goal).run {
                                    when {
                                        expectedSolutions.first() is Solution.Yes -> hasSolutions({ no() })
                                        expectedSolutions.first() is Solution.No -> hasSolutions({ yes() })
                                        else -> to(expectedSolutions.changeQueriesTo(this))
                                    }
                                },
                                not(goal).run {
                                    when {
                                        expectedSolutions.first() is Solution.Yes -> hasSolutions({ no() })
                                        expectedSolutions.first() is Solution.No -> hasSolutions({ yes() })
                                        else -> to(expectedSolutions.changeQueriesTo(this))
                                    }
                                },
                                naf(naf(goal)).run {
                                    when {
                                        expectedSolutions.first() is Solution.Yes -> hasSolutions({ yes() })
                                        expectedSolutions.first() is Solution.No -> hasSolutions({ no() })
                                        else -> to(expectedSolutions.changeQueriesTo(this))
                                    }
                                },
                                not(not(goal)).run {
                                    when {
                                        expectedSolutions.first() is Solution.Yes -> hasSolutions({ yes() })
                                        expectedSolutions.first() is Solution.No -> hasSolutions({ no() })
                                        else -> to(expectedSolutions.changeQueriesTo(this))
                                    }
                                },
                            )
                        }
                    }.forEach { (database, goalToSolutions) ->
                        assertConcurrentSolverSolutionsCorrect(
                            solverWithDefaultBuiltins(staticKb = database),
                            goalToSolutions,
                            mediumDuration,
                        )
                    }
            }
        }

        // If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution]

        scenario("solver/if-then-rule") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.ifThenStandardExampleTheory),
                ifThenStandardExampleTheoryNotableGoalToSolution,
                mediumDuration,
            )
        }

        // If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution]

        scenario("solver/if-then-else-rule") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                ifThenElseStandardExampleNotableGoalToSolution,
                mediumDuration,
            )
        }

        // Test with [customRangeListGeneratorTheoryNotableGoalToSolution]

        scenario("solver/numbers-range-list-generation") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(staticKb = TestingClauseTheories.customRangeListGeneratorTheory),
                customRangeListGeneratorTheoryNotableGoalToSolution,
                mediumDuration,
            )
        }

        scenario("solver/conjunction") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a" impliedBy ("b" and "c") },
                                { "b" },
                                { "c" },
                            ),
                    )
                val query = atomOf("a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/conjunction-with-unification") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a"(X) impliedBy ("b"(X) and "c"(X)) },
                                { "b"(1) },
                                { "c"(1) },
                            ),
                    )
                val query = "a"(N)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes(N to 1))

                expected.assertingEquals(solutions)
            }
        }

        // Test presence of correct built-ins

        scenario("solver/builtin-api") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                with(solver) {
                    assertHasPredicateInAPI("!", 0)
                    assertHasPredicateInAPI("call", 1)
                    assertHasPredicateInAPI("catch", 3)
                    assertHasPredicateInAPI("throw", 1)
                    assertHasPredicateInAPI(",", 2)
                    assertHasPredicateInAPI("\\+", 1)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Abolish)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.Append.SIGNATURE)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Arg)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticEqual)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThan)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThanOrEqualTo)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThan)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThanOrEqualTo)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticNotEqual)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.Arrow)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Assert)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.AssertA)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.AssertZ)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Atom)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.AtomChars)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.AtomCodes)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.AtomConcat)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.AtomLength)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Atomic)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.BagOf)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Between)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Callable)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Clause)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Compound)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.CopyTerm)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.CurrentOp)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.CurrentPrologFlag)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.FindAll)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Float)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Functor)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.GetDurable)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Ground)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Halt)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Halt1)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Integer)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Is)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.Member.SIGNATURE)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Natural)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.NewLine)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.NonVar)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.Not)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.NotUnifiableWith)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Number)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.NumberChars)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.NumberCodes)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.Once)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Op)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Repeat)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Retract)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.RetractAll)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Reverse)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.Semicolon.SIGNATURE)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.SetDurable)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.SetFlag)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.SetOf)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.rule.SetPrologFlag)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Sleep)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.SubAtom)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThan)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThanOrEqualTo)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermIdentical)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThan)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThanOrEqualTo)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermNotIdentical)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermNotSame)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.TermSame)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.UnifiesWith)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Univ)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Var)
                    assertHasPredicateInAPI(it.unibo.tuprolog.solve.stdlib.primitive.Write)
                }
            }
        }

        scenario("solver/disjunction") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a" impliedBy ("b" or "c") },
                                { "b" },
                                { "c" },
                            ),
                    )
                val query = atomOf("a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(sequenceOf(query.yes(), query.yes()))

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/failure") {
            // TODO: 12/11/2019 enrich this test after solving #51
            logicProgramming {
                val solver = solverWithDefaultBuiltins()
                val query = atomOf("a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.no())

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/disjunction-with-unification") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a"(X) impliedBy ("b"(X) or "c"(X)) },
                                { "b"(1) },
                                { "c"(2) },
                            ),
                    )
                val query = "a"(N)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(sequenceOf(query.yes(N to 1), query.yes(N to 2)))

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/conjunction-of-conjunctions") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a" impliedBy (tupleOf("b", "c") and tupleOf("d", "e")) },
                                { "b" },
                                { "c" },
                                { "d" },
                                { "e" },
                            ),
                    )
                val query = atomOf("a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/member") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                assertConcurrentSolverSolutionsCorrect(solver, CustomTheories.memberGoalToSolution, mediumDuration)
            }
        }

        scenario("solver/basic-backtracking1") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a"(X) impliedBy ("b"(X) and "c"(X)) },
                                { "b"(1) },
                                { "b"(2) impliedBy cut },
                                { "b"(3) },
                                { "c"(2) },
                                { "c"(3) },
                            ),
                    )
                val query = "a"(N)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes(N to 2))

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/basic-backtracking2") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a"(X) impliedBy ("c"(X) and "b"(X)) },
                                { "b"(2) impliedBy cut },
                                { "b"(3) },
                                { "c"(3) },
                                { "c"(2) },
                            ),
                    )
                val query = "a"(N)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(sequenceOf(query.yes(N to 3), query.yes(N to 2)))

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/basic-backtracking3") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a"(X) impliedBy (("b"(X) and cut) and "c"(X)) },
                                { "b"(2) },
                                { "b"(3) },
                                { "c"(2) },
                                { "c"(3) },
                            ),
                    )
                val query = "a"(N)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes(N to 2))

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/basic-backtracking4") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        staticKb =
                            theory(
                                { "a"(X) impliedBy ("b"(X) and (cut and "c"(X))) },
                                { "b"(2) },
                                { "b"(3) },
                                { "c"(2) },
                                { "c"(3) },
                            ),
                    )
                val query = "a"(N)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes(N to 2))

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/assert-rules") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = assertz("f"(2) impliedBy false) and asserta("f"(1) impliedBy true)

                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)

                assertEquals(
                    listOf(
                        factOf(structOf("f", numOf(1))),
                        ruleOf(structOf("f", numOf(2)), atomOf("false")),
                    ),
                    solver.dynamicKb.toList(),
                )
            }
        }

        scenario("solver/retract") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        dynamicKb =
                            theoryOf(
                                factOf(structOf("f", numOf(1))),
                                ruleOf(structOf("f", numOf(2)), atomOf("false")),
                            ),
                    )

                val query = retract("f"(X)) // retract(f(X))

                val solutions = fromSequence(solver.solve(query, longDuration))
                val expected = fromSequence(sequenceOf(query.yes(X to 1), query.yes(X to 2)))

                expected.assertingEquals(solutions)

                assertEquals(
                    listOf(),
                    solver.dynamicKb.toList(),
                )
                assertEquals(0L, solver.dynamicKb.size)
            }
        }

        scenario("solver/natural") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = natural(X) and natural(X)

                val n = 100

                val solutions = fromSequence(solver.solve(query, mediumDuration).take(n).toList())
                val expected = fromSequence((0 until n).map { query.yes(X to it) })

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/functor") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                var query = functor("a"("b", "c"), X, Y)

                var solutions = fromSequence(solver.solve(query, mediumDuration))
                var expected = fromSequence(query.yes(X to "a", Y to 2))

                expected.assertingEquals(solutions)

                query = functor("a"("b", "c"), "a", Y)

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(Y to 2))

                expected.assertingEquals(solutions)

                query = functor("a"("b", "c"), X, 2)

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(X to "a"))

                expected.assertingEquals(solutions)

                query = functor(X, "a", 2)

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(X to structOf("a", anonymous(), anonymous())))

                expected.assertingEquals(solutions)

                query = functor(X, Y, 2)

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                variable = Y,
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)

                query = functor(X, "a", "2")

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                TypeError.Expected.INTEGER,
                                atomOf("2"),
                                2,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/univ") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                var query = "a"("b", "c") univ X

                var solutions = fromSequence(solver.solve(query, mediumDuration))
                var expected = fromSequence(query.yes(X to logicListOf("a", "b", "c")))

                expected.assertingEquals(solutions)

                query = X univ logicListOf("a", "b", "c")

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(X to structOf("a", "b", "c")))

                expected.assertingEquals(solutions)

                query = X univ Y

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("=..", 2),
                                variable = X,
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)

                query = "a"("b", "c") univ "a"

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("=..", 2),
                                TypeError.Expected.LIST,
                                atomOf("a"),
                                1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/retract-all") {
            logicProgramming {
                val solver =
                    solverWithDefaultBuiltins(
                        dynamicKb =
                            theoryOf(
                                factOf(structOf("f", numOf(1))),
                                ruleOf(structOf("f", numOf(2)), atomOf("false")),
                            ),
                    )

                var query = retractall("f"(X))

                var solutions = fromSequence(solver.solve(query, longDuration))
                var expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)

                assertEquals(
                    listOf(),
                    solver.dynamicKb.toList(),
                )
                assertEquals(0L, solver.dynamicKb.size)

                query = retractall("f"(X))

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes())

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/append") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                var query = append(logicListOf(1, 2, 3), logicListOf(4, 5, 6), X)

                var solutions = fromSequence(solver.solve(query, mediumDuration))
                var expected = fromSequence(query.yes(X to logicListOf(1, 2, 3, 4, 5, 6)))

                expected.assertingEquals(solutions)

                query = append(logicListOf(1, 2, 3), X, logicListOf(1, 2, 3, 4, 5, 6))

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.yes(X to logicListOf(4, 5, 6)))

                expected.assertingEquals(solutions)

                query = append(X, X, logicListOf(1, 2, 3, 4, 5, 6))

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected = fromSequence(query.no())

                expected.assertingEquals(solutions)

                query = append(X, Y, logicListOf(1, 2, 3, 4, 5, 6))

                solutions = fromSequence(solver.solve(query, mediumDuration))
                expected =
                    fromSequence(
                        sequenceOf(
                            query.yes(X to emptyLogicList, Y to logicListOf(1, 2, 3, 4, 5, 6)),
                            query.yes(X to logicListOf(1), Y to logicListOf(2, 3, 4, 5, 6)),
                            query.yes(X to logicListOf(1, 2), Y to logicListOf(3, 4, 5, 6)),
                            query.yes(X to logicListOf(1, 2, 3), Y to logicListOf(4, 5, 6)),
                            query.yes(X to logicListOf(1, 2, 3, 4), Y to logicListOf(5, 6)),
                            query.yes(X to logicListOf(1, 2, 3, 4, 5), Y to logicListOf(6)),
                            query.yes(X to logicListOf(1, 2, 3, 4, 5, 6), Y to emptyLogicList),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("solver/term-greater-than") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                TestingTermOperators.greaterThanTesting,
                mediumDuration,
            )
        }

        scenario("solver/term-same") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                TestingTermOperators.equalTesting,
                mediumDuration,
            )
        }

        scenario("solver/term-lower-than") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                TestingTermOperators.lowerThanTesting,
                mediumDuration,
            )
        }

        scenario("solver/term-greater-than-or-equal") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                TestingTermOperators.greaterThanOrEqualTesting,
                mediumDuration,
            )
        }

        scenario("solver/term-lower-than-or-equal") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                TestingTermOperators.lowerThanOrEqualTesting,
                mediumDuration,
            )
        }

        scenario("solver/term-not-same") {
            assertConcurrentSolverSolutionsCorrect(
                solverWithDefaultBuiltins(),
                TestingTermOperators.notEqualTesting,
                mediumDuration,
            )
        }
    }

private fun ConcurrentContractScope.testAssert(
    suffix: String,
    inverse: Boolean,
) {
    logicProgramming {
        val solver = solverWithDefaultBuiltins()
        val assertX = "assert$suffix"

        val query =
            assertX("f"(1)) and
                assertX("f"(2)) and
                assertX("f"(3)) and
                "f"(X)

        val solutions = fromSequence(solver.solve(query, mediumDuration))
        val ints = if (inverse) (3 downTo 1) else (1..3)
        val expected = fromSequence((ints).map { query.yes(X to it) })

        expected.assertingEquals(solutions)
        ints.forEach {
            assertTrue {
                "f"(it) in solver.dynamicKb
            }
        }
    }
}

private val callErrorSignature = Signature("ensure_executable", 1)
private val nafErrorSignature = Signature("ensure_executable", 1)
private val notErrorSignature = Signature("ensure_executable", 1)
