package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.dsl.theory.prolog
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
import it.unibo.tuprolog.solve.SolverFactory
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
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.assertSolverSolutionsCorrect
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface TestConcurrentSolver<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    val callErrorSignature: Signature
    val nafErrorSignature: Signature
    val notErrorSignature: Signature

    fun testUnknownFlag2() {
        prolog {
            val theory = theoryOf(
                rule {
                    "ancestor"(X, Y) `if` "parent"(X, Y)
                },
                rule {
                    "ancestor"(X, Y) `if` ("parent"(X, Z) and "ancestor"(Z, Y))
                },
                fact { "parent"("abraham", "isaac") },
                fact { "parent"("isaac", "jacob") },
                fact { "parent"("jacob", "joseph") }
            )

            val observedWarnings = mutableListOf<Warning>()

            var solver = solverWithDefaultBuiltins(
                staticKb = theory,
                flags = FlagStore.of(Unknown to Unknown.WARNING),
                warnings = OutputChannel.of {
                    observedWarnings.add(it)
                }
            )

            val query = "ancestor"("abraham", X)

            val expectedSolutions = ktListOf(
                query.yes(X to "isaac"),
                query.yes(X to "jacob"),
                query.yes(X to "joseph"),
                query.no()
            )

            assertSolutionEquals(expectedSolutions, solver.solve(query).toList())
            assertEquals(mutableListOf(), observedWarnings)

            solver = solverWithDefaultBuiltins(
                staticKb = theory,
                flags = FlagStore.of(Unknown to Unknown.ERROR),
                warnings = OutputChannel.of {
                    observedWarnings.add(it)
                }
            )

            assertSolutionEquals(expectedSolutions, solver.solve(query).toList())
            assertEquals(mutableListOf(), observedWarnings)

            solver = solverWithDefaultBuiltins(
                staticKb = theory,
                flags = FlagStore.of(Unknown to Unknown.FAIL),
                warnings = OutputChannel.of {
                    observedWarnings.add(it)
                }
            )

            assertSolutionEquals(expectedSolutions, solver.solve(query).toList())
            assertEquals(mutableListOf(), observedWarnings)
        }
    }

    fun testUnknownFlag1() {
        prolog {
            val query = "missing_predicate"(X)

            val observedWarnings = mutableListOf<Warning>()

            var solver = solverWithDefaultBuiltins(
                flags = FlagStore.of(Unknown to Unknown.ERROR),
                warnings = OutputChannel.of {
                    observedWarnings.add(it)
                }
            )

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            query.extractSignature()
                        )
                    )
                ),
                solver.solve(query).toList()
            )
            assertTrue { observedWarnings.isEmpty() }

            solver = solverWithDefaultBuiltins(
                flags = FlagStore.of(Unknown to Unknown.WARNING),
                warnings = OutputChannel.of {
                    observedWarnings.add(it)
                }
            )

            assertSolutionEquals(
                ktListOf(query.no()),
                solver.solve(query).toList()
            )
            assertEquals(1, observedWarnings.size)
            assertTrue { observedWarnings[0] is MissingPredicate }
            assertEquals(query.extractSignature(), (observedWarnings[0] as MissingPredicate).signature)

            solver = solverWithDefaultBuiltins(
                flags = FlagStore.of(Unknown to Unknown.FAIL),
                warnings = OutputChannel.of {
                    observedWarnings.add(it)
                }
            )

            assertSolutionEquals(
                ktListOf(query.no()),
                solver.solve(query).toList()
            )
            assertTrue { observedWarnings.size == 1 }
        }
    }

    /** Test presence of correct built-ins */
    fun testBuiltinApi() {
        prolog {
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

    private fun testAssert(suffix: String, inverse: Boolean) {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val assertX = "assert$suffix"

            val query = assertX("f"(1)) and
                assertX("f"(2)) and
                assertX("f"(3)) and
                "f"(X)

            val solutions = solver.solve(query, mediumDuration).toList()
            val ints = if (inverse) (3 downTo 1) else (1..3)

            assertSolutionEquals(
                (ints).map { query.yes(X to it) },
                solutions
            )

            ints.forEach {
                assertTrue {
                    "f"(it) in solver.dynamicKb
                }
            }
        }
    }

    fun testAssert() {
        testAssert("", false)
    }

    fun testAssertZ() {
        testAssert("z", false)
    }

    fun testAssertA() {
        testAssert("a", true)
    }

    fun testWrite() {
        val outputs = mutableListOf<String>()
        prolog {
            val solver = solverWithDefaultBuiltins()

            solver.standardOutput.addListener { outputs += it!! }

            val terms = ktListOf(
                atomOf("atom"),
                atomOf("a string"),
                varOf("A_Var"),
                numOf(1),
                numOf(2.1),
                "f"("x")
            )

            val query = tupleOf(terms.map { write(it) }.append(nl))

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            assertEquals(
                terms.map { it.format(TermFormatter.default()) }.append("\n"),
                outputs
            )
        }
    }

    fun testStandardOutput() {
        val outputs = mutableListOf<String>()
        prolog {
            val solver = solverWithDefaultBuiltins()

            with(solver.standardOutput) {
                addListener { outputs += it!! }
                write("a")
            }

            val query = write("b") and write("c") and write("d") and nl

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )

            solver.standardOutput.write("e")

            assertEquals(
                ktListOf("a", "b", "c", "d", "\n", "e"),
                outputs
            )
        }
    }

    fun testFindAll() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theoryOf(
                    fact { "a"(1) },
                    fact { "a"(2) },
                    fact { "a"(3) }
                )
            )

            var query = findall(N, "a"(N), L)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(L to listOf(1, 2, 3))),
                solutions
            )

            query = findall(`_`, false, L)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(L to emptyList)),
                solutions
            )

            query = findall(`_`, G, `_`)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("findall", 3),
                            variable = G,
                            index = 1
                        )
                    )
                ),
                solutions
            )
        }
    }

    fun testSideEffectsPersistentAfterBacktracking1() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                dynamicKb = theoryOf(
                    fact { "f"(1) },
                    fact { "f"(2) },
                    fact { "f"(3) }
                ),
                staticKb = theoryOf(
                    clause { "getf"(F) `if` findall(X, "f"(X), F) },
                    clause { "getg"(G) `if` findall(X, "g"(X), G) },
                    clause {
                        "ftog"(F, G) `if` (
                            retract("f"(X)) and
                                assert("g"(X)) and
                                "getf"(F) and
                                "getg"(G)
                            )
                    }
                )
            )

            val query = "ftog"(X, Y)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(X to listOf(2, 3), Y to listOf(1)),
                    query.yes(X to listOf(3), Y to listOf(1, 2)),
                    query.yes(X to emptyList, Y to listOf(1, 2, 3))
                ),
                solutions
            )
        }
    }

    /** Test `true` goal */
    fun testTrue() {
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = truthOf(true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    /** Test with [lessThan500MsGoalToSolution] */
    fun testTimeout1() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(
                staticKb = TimeRelatedTheories.timeRelatedTheory
            ),
            goalToSolutions = lessThan500MsGoalToSolution,
            maxDuration = 400L
        )
    }

    /** Test with [slightlyMoreThan500MsGoalToSolution] */
    fun testTimeout2() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(
                staticKb = TimeRelatedTheories.timeRelatedTheory
            ),
            goalToSolutions = slightlyMoreThan500MsGoalToSolution,
            maxDuration = 599L
        )
    }

    /** Test with [slightlyMoreThan600MsGoalToSolution] */
    fun testTimeout3() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(
                staticKb = TimeRelatedTheories.timeRelatedTheory
            ),
            goalToSolutions = slightlyMoreThan600MsGoalToSolution,
            maxDuration = 699L
        )
    }

    /** Test with [slightlyMoreThan700MsGoalToSolution] */
    fun testTimeout4() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(
                staticKb = TimeRelatedTheories.timeRelatedTheory
            ),
            goalToSolutions = slightlyMoreThan700MsGoalToSolution,
            maxDuration = 799L
        )
    }

    /** Test with [ifThen1ToSolution] */
    fun testIfThen1() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory1),
            goalToSolutions = ifThen1ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [ifThenElse1ToSolution] */
    fun testIfThenElse1() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory1),
            goalToSolutions = ifThenElse1ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [ifThenElse2ToSolution] */
    fun testIfThenElse2() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory2),
            goalToSolutions = ifThenElse2ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [ifThen2ToSolution] */
    fun testIfThen2() {
        assertSolverSolutionsCorrect(
            solver = solverWithDefaultBuiltins(staticKb = CustomTheories.ifThenTheory2),
            goalToSolutions = ifThen2ToSolution,
            maxDuration = mediumDuration
        )
    }

    /** Test with [simpleFactTheoryNotableGoalToSolutions] */
    fun testUnification() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.simpleFactTheory),
            simpleFactTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [simpleCutTheoryNotableGoalToSolutions] */
    fun testSimpleCutAlternatives() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.simpleCutTheory),
            simpleCutTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions] */
    fun testCutAndConjunction() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.simpleCutAndConjunctionTheory),
            simpleCutAndConjunctionTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions] */
    fun testCutConjunctionAndBacktracking() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.cutConjunctionAndBacktrackingTheory),
            cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
            mediumDuration
        )
    }

    /** Test with [infiniteComputationTheoryNotableGoalToSolution] */
    fun testMaxDurationParameterAndTimeOutException() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.infiniteComputationTheory),
            infiniteComputationTheoryNotableGoalToSolution,
            shortDuration
        )
    }

    /** Test with [prologStandardExampleTheoryNotableGoalToSolution] */
    fun testPrologStandardSearchTreeExample() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.prologStandardExampleTheory),
            prologStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution] */
    fun testPrologStandardSearchTreeWithCutExample() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.prologStandardExampleWithCutTheory),
            prologStandardExampleWithCutTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [customReverseListTheoryNotableGoalToSolution] */
    fun testBacktrackingWithCustomReverseListImplementation() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.customReverseListTheory),
            customReverseListTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [conjunctionStandardExampleTheoryNotableGoalToSolution] */
    fun testWithPrologStandardConjunctionExamples() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.conjunctionStandardExampleTheory),
            conjunctionStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties */
    fun testConjunctionProperties() {
        prolog {
            val allDatabasesWithGoalsAndSolutions by lazy {
                TestingClauseTheories.allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                    callErrorSignature,
                    nafErrorSignature,
                    notErrorSignature
                )
                    .mapValues { (_, listOfGoalToSolutions) ->
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
                    solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration
                )
            }
        }
    }

    /** Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution] */
    fun testCallPrimitive() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.callStandardExampleTheory),
            callStandardExampleTheoryGoalsToSolution(callErrorSignature),
            mediumDuration
        )

        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            callTestingGoalsToSolutions(callErrorSignature),
            mediumDuration
        )
    }

    /** A test in which all testing goals are called through the Call primitive */
    fun testCallPrimitiveTransparency() {
        prolog {
            TestingClauseTheories.allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                callErrorSignature,
                nafErrorSignature,
                notErrorSignature
            )
                .mapValues { (_, listOfGoalToSolutions) ->
                    listOfGoalToSolutions.map { (goal, expectedSolutions) ->
                        call(goal).run { to(expectedSolutions.changeQueriesTo(this)) }
                    }
                }.forEach { (database, goalToSolutions) ->
                    assertSolverSolutionsCorrect(
                        solverWithDefaultBuiltins(staticKb = database),
                        goalToSolutions,
                        mediumDuration
                    )
                }
        }
    }

    /** Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution] */
    fun testCatchPrimitive() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.catchAndThrowTheoryExample),
            catchAndThrowTheoryExampleNotableGoalToSolution,
            mediumDuration
        )

        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            catchTestingGoalsToSolutions,
            mediumDuration
        )
    }

    /** A test in which all testing goals are called through the Catch primitive */
    fun testCatchPrimitiveTransparency() {
        prolog {
            fun Struct.containsHaltPrimitive(): Boolean = when (functor) {
                "halt" -> true
                else -> argsSequence.filterIsInstance<Struct>().any { it.containsHaltPrimitive() }
            }

            TestingClauseTheories.allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                callErrorSignature,
                nafErrorSignature,
                notErrorSignature
            )
                .mapValues { (_, listOfGoalToSolutions) ->
                    listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                        ktListOf(
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
                            }
                        )
                    }
                }.forEach { (database, goalToSolutions) ->
                    assertSolverSolutionsCorrect(
                        solverWithDefaultBuiltins(staticKb = database),
                        goalToSolutions,
                        mediumDuration
                    )
                }
        }
    }

    /** Halt primitive testing with [haltTestingGoalsToSolutions] */
    fun testHaltPrimitive() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            haltTestingGoalsToSolutions,
            mediumDuration
        )
    }

    /** Not rule testing with [notStandardExampleTheoryNotableGoalToSolution] */
    fun testNotPrimitive() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.notStandardExampleTheory),
            notStandardExampleTheoryNotableGoalToSolution(nafErrorSignature, notErrorSignature),
            mediumDuration
        )
    }

    /** A test in which all testing goals are called through the Not rule */
    fun testNotModularity() {
        prolog {
            TestingClauseTheories.allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                callErrorSignature,
                nafErrorSignature,
                notErrorSignature
            )
                .mapValues { (_, listOfGoalToSolutions) ->
                    listOfGoalToSolutions.flatMap { (goal, expectedSolutions) ->
                        ktListOf(
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
                            }
                        )
                    }
                }.forEach { (database, goalToSolutions) ->
                    assertSolverSolutionsCorrect(
                        solverWithDefaultBuiltins(staticKb = database),
                        goalToSolutions,
                        mediumDuration
                    )
                }
        }
    }

    /** If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution] */
    fun testIfThenRule() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = PrologStandardExampleTheories.ifThenStandardExampleTheory),
            ifThenStandardExampleTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    /** If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution] */
    fun testIfThenElseRule() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            ifThenElseStandardExampleNotableGoalToSolution,
            mediumDuration
        )
    }

    /** Test with [customRangeListGeneratorTheoryNotableGoalToSolution] */
    fun testNumbersRangeListGeneration() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(staticKb = TestingClauseTheories.customRangeListGeneratorTheory),
            customRangeListGeneratorTheoryNotableGoalToSolution,
            mediumDuration
        )
    }

    fun testFailure() {
        // TODO: 12/11/2019 enrich this test after solving #51
        prolog {
            val solver = solverWithDefaultBuiltins()
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.no()
                ),
                solutions
            )
        }
    }

    fun testBasicBacktracking1() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"(X) impliedBy ("b"(X) and "c"(X)) },
                    { "b"(1) },
                    { "b"(2) impliedBy cut },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(N to 2)
                ),
                solutions
            )
        }
    }

    fun testBasicBacktracking2() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"(X) impliedBy ("c"(X) and "b"(X)) },
                    { "b"(2) impliedBy cut },
                    { "b"(3) },
                    { "c"(3) },
                    { "c"(2) }
                )
            )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { ktListOf(yes(N to 3), yes(N to 2)) },
                solutions
            )
        }
    }

    fun testBasicBacktracking3() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"(X) impliedBy (("b"(X) and cut) and "c"(X)) },
                    { "b"(2) },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(N to 2)
                ),
                solutions
            )
        }
    }

    fun testBasicBacktracking4() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"(X) impliedBy ("b"(X) and (cut and "c"(X))) },
                    { "b"(2) },
                    { "b"(3) },
                    { "c"(2) },
                    { "c"(3) }
                )
            )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(N to 2)
                ),
                solutions
            )
        }
    }

    fun testConjunction() {
        prolog {
            val solver = solverWithDefaultBuiltins(
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
                ),
                solutions
            )
        }
    }

    fun testConjunctionOfConjunctions() {
        prolog {
            val solver = solverWithDefaultBuiltins(
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
                ),
                solutions
            )
        }
    }

    fun testConjunctionWithUnification() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"(X) impliedBy ("b"(X) and "c"(X)) },
                    { "b"(1) },
                    { "c"(1) }
                )
            )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(N to 1)
                ),
                solutions
            )
        }
    }

    fun testDisjunction() {
        prolog {
            val solver = solverWithDefaultBuiltins(
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

    fun testDisjunctionWithUnification() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                staticKb = theory(
                    { "a"(X) impliedBy ("b"(X) or "c"(X)) },
                    { "b"(1) },
                    { "c"(2) }
                )
            )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { ktListOf(yes(N to 1), yes(N to 2)) },
                solutions
            )

            assertEquals(2, solutions.size)
        }
    }

    fun testMember() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            assertSolverSolutionsCorrect(solver, CustomTheories.memberGoalToSolution, mediumDuration)
        }
    }

    fun testAssertRules() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = assertz("f"(2) impliedBy false) and asserta("f"(1) impliedBy true)

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

    fun testRetract() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                dynamicKb = theoryOf(
                    factOf(structOf("f", numOf(1))),
                    ruleOf(structOf("f", numOf(2)), atomOf("false"))
                )
            )

            val query = retract("f"(X)) // retract(f(X))

            val solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(X to 1),
                    query.yes(X to 2)

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

    fun testNatural() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = natural(X) and natural(X)

            val n = 100

            val solutions = solver.solve(query, mediumDuration).take(n).toList()

            assertSolutionEquals(
                (0 until n).map { query.yes(X to it) },
                solutions
            )
        }
    }

    fun testFunctor() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = functor("a"("b", "c"), X, Y)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to "a", Y to 2)),
                solutions
            )

            query = functor("a"("b", "c"), "a", Y)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(Y to 2)),
                solutions
            )

            query = functor("a"("b", "c"), X, 2)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to "a")),
                solutions
            )

            query = functor(X, "a", 2)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to structOf("a", anonymous(), anonymous()))),
                solutions
            )

            query = functor(X, Y, 2)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            variable = Y,
                            index = 1
                        )
                    )
                ),
                solutions
            )

            query = functor(X, "a", "2")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.INTEGER,
                            atomOf("2"),
                            2
                        )
                    )
                ),
                solutions
            )
        }
    }

    fun testUniv() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = "a"("b", "c") univ X
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to listOf("a", "b", "c"))),
                solutions
            )

            query = X univ listOf("a", "b", "c")
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to structOf("a", "b", "c"))),
                solutions
            )

            query = X univ Y
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=..", 2),
                            variable = X,
                            index = 0
                        )
                    )
                ),
                solutions
            )

            query = "a"("b", "c") univ "a"
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=..", 2),
                            TypeError.Expected.LIST,
                            atomOf("a"),
                            1
                        )
                    )
                ),
                solutions
            )
        }
    }

    fun testRetractAll() {
        prolog {
            val solver = solverWithDefaultBuiltins(
                dynamicKb = theoryOf(
                    factOf(structOf("f", numOf(1))),
                    ruleOf(structOf("f", numOf(2)), atomOf("false"))
                )
            )

            var query = retractall("f"(X))

            var solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes()
                ),
                solutions
            )

            assertEquals(
                ktListOf(),
                solver.dynamicKb.toList()
            )
            assertEquals(0L, solver.dynamicKb.size)

            query = retractall("f"(X))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes()
                ),
                solutions
            )
        }
    }

    fun testAppend() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            var query = append(listOf(1, 2, 3), listOf(4, 5, 6), X)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to listOf(1, 2, 3, 4, 5, 6))),
                solutions
            )

            query = append(listOf(1, 2, 3), X, listOf(1, 2, 3, 4, 5, 6))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes(X to listOf(4, 5, 6))),
                solutions
            )

            query = append(X, X, listOf(1, 2, 3, 4, 5, 6))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )

            query = append(X, Y, listOf(1, 2, 3, 4, 5, 6))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(X to emptyList, Y to listOf(1, 2, 3, 4, 5, 6)),
                    query.yes(X to listOf(1), Y to listOf(2, 3, 4, 5, 6)),
                    query.yes(X to listOf(1, 2), Y to listOf(3, 4, 5, 6)),
                    query.yes(X to listOf(1, 2, 3), Y to listOf(4, 5, 6)),
                    query.yes(X to listOf(1, 2, 3, 4), Y to listOf(5, 6)),
                    query.yes(X to listOf(1, 2, 3, 4, 5), Y to listOf(6)),
                    query.yes(X to listOf(1, 2, 3, 4, 5, 6), Y to emptyList)
                ),
                solutions
            )
        }
    }

    fun testTermGreaterThan() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            TestingTermOperators.greaterThanTesting,
            mediumDuration
        )
    }

    fun testTermGreaterThanOrEqual() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            TestingTermOperators.greaterThanOrEqualTesting,
            mediumDuration
        )
    }

    fun testTermSame() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            TestingTermOperators.equalTesting,
            mediumDuration
        )
    }

    fun testTermNotSame() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            TestingTermOperators.notEqualTesting,
            mediumDuration
        )
    }

    fun testTermLowerThan() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            TestingTermOperators.lowerThanTesting,
            mediumDuration
        )
    }

    fun testTermLowerThanOrEqual() {
        assertSolverSolutionsCorrect(
            solverWithDefaultBuiltins(),
            TestingTermOperators.lowerThanOrEqualTesting,
            mediumDuration
        )
    }
}