package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.dsl.theory.logicProgramming
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
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingTermOperators.equalTesting
import it.unibo.tuprolog.solve.TestingTermOperators.greaterThanOrEqualTesting
import it.unibo.tuprolog.solve.TestingTermOperators.greaterThanTesting
import it.unibo.tuprolog.solve.TestingTermOperators.lowerThanOrEqualTesting
import it.unibo.tuprolog.solve.TestingTermOperators.lowerThanTesting
import it.unibo.tuprolog.solve.TestingTermOperators.notEqualTesting
import it.unibo.tuprolog.solve.TimeRelatedTheories.lessThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan500MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan600MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.slightlyMoreThan700MsGoalToSolution
import it.unibo.tuprolog.solve.TimeRelatedTheories.timeRelatedTheory
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.exception.warning.MissingPredicate
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.solve.flags.Unknown.ERROR
import it.unibo.tuprolog.solve.flags.Unknown.FAIL
import it.unibo.tuprolog.solve.flags.Unknown.WARNING
import it.unibo.tuprolog.solve.stdlib.primitive.Abolish
import it.unibo.tuprolog.solve.stdlib.primitive.Arg
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticEqual
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticNotEqual
import it.unibo.tuprolog.solve.stdlib.primitive.Assert
import it.unibo.tuprolog.solve.stdlib.primitive.AssertA
import it.unibo.tuprolog.solve.stdlib.primitive.AssertZ
import it.unibo.tuprolog.solve.stdlib.primitive.Atom
import it.unibo.tuprolog.solve.stdlib.primitive.AtomChars
import it.unibo.tuprolog.solve.stdlib.primitive.AtomCodes
import it.unibo.tuprolog.solve.stdlib.primitive.AtomConcat
import it.unibo.tuprolog.solve.stdlib.primitive.AtomLength
import it.unibo.tuprolog.solve.stdlib.primitive.Atomic
import it.unibo.tuprolog.solve.stdlib.primitive.BagOf
import it.unibo.tuprolog.solve.stdlib.primitive.Between
import it.unibo.tuprolog.solve.stdlib.primitive.Callable
import it.unibo.tuprolog.solve.stdlib.primitive.Clause
import it.unibo.tuprolog.solve.stdlib.primitive.Compound
import it.unibo.tuprolog.solve.stdlib.primitive.CopyTerm
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentOp
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import it.unibo.tuprolog.solve.stdlib.primitive.FindAll
import it.unibo.tuprolog.solve.stdlib.primitive.Functor
import it.unibo.tuprolog.solve.stdlib.primitive.GetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.GetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.GetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.Ground
import it.unibo.tuprolog.solve.stdlib.primitive.Halt
import it.unibo.tuprolog.solve.stdlib.primitive.Halt1
import it.unibo.tuprolog.solve.stdlib.primitive.Integer
import it.unibo.tuprolog.solve.stdlib.primitive.Is
import it.unibo.tuprolog.solve.stdlib.primitive.Natural
import it.unibo.tuprolog.solve.stdlib.primitive.NewLine
import it.unibo.tuprolog.solve.stdlib.primitive.NonVar
import it.unibo.tuprolog.solve.stdlib.primitive.NotUnifiableWith
import it.unibo.tuprolog.solve.stdlib.primitive.Number
import it.unibo.tuprolog.solve.stdlib.primitive.NumberChars
import it.unibo.tuprolog.solve.stdlib.primitive.NumberCodes
import it.unibo.tuprolog.solve.stdlib.primitive.Op
import it.unibo.tuprolog.solve.stdlib.primitive.Repeat
import it.unibo.tuprolog.solve.stdlib.primitive.Retract
import it.unibo.tuprolog.solve.stdlib.primitive.RetractAll
import it.unibo.tuprolog.solve.stdlib.primitive.Reverse
import it.unibo.tuprolog.solve.stdlib.primitive.SetDurable
import it.unibo.tuprolog.solve.stdlib.primitive.SetEphemeral
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import it.unibo.tuprolog.solve.stdlib.primitive.SetOf
import it.unibo.tuprolog.solve.stdlib.primitive.SetPersistent
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep
import it.unibo.tuprolog.solve.stdlib.primitive.SubAtom
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermIdentical
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermNotIdentical
import it.unibo.tuprolog.solve.stdlib.primitive.TermNotSame
import it.unibo.tuprolog.solve.stdlib.primitive.TermSame
import it.unibo.tuprolog.solve.stdlib.primitive.UnifiesWith
import it.unibo.tuprolog.solve.stdlib.primitive.Univ
import it.unibo.tuprolog.solve.stdlib.primitive.Var
import it.unibo.tuprolog.solve.stdlib.primitive.Write
import it.unibo.tuprolog.solve.stdlib.rule.Append
import it.unibo.tuprolog.solve.stdlib.rule.Arrow
import it.unibo.tuprolog.solve.stdlib.rule.CurrentPrologFlag
import it.unibo.tuprolog.solve.stdlib.rule.Member
import it.unibo.tuprolog.solve.stdlib.rule.Not
import it.unibo.tuprolog.solve.stdlib.rule.Once
import it.unibo.tuprolog.solve.stdlib.rule.Semicolon
import it.unibo.tuprolog.solve.stdlib.rule.SetPrologFlag
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import it.unibo.tuprolog.solve.stdlib.primitive.Float as FloatPrimitive
import kotlin.collections.plus as append

/** The main, comprehensive conformance suite for a [Solver] implementation: control-flow constructs (conjunction, disjunction, if-then(-else), cut, `call/1`, `catch/3`, `not`/`\+`), timeouts, side effects (`assert`, `write`, standard output, `findall/3`), and a battery of standard Prolog examples (search trees, backtracking, recursive list processing, term ordering, etc.) drawn from [PrologStandardExampleTheories] and [TestingClauseTheories]. */
fun solverContract(
    solverFactory: SolverFactory,
    callErrorSignature: Signature = Signature("call", 1),
    nafErrorSignature: Signature = Signature("\\+", 1),
    notErrorSignature: Signature = Signature("not", 1),
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    scenario("solver/unknown-flag1") {
        logicProgramming {
            val query = "missing_predicate"(X)

            val observedWarnings = mutableListOf<Warning>()

            var solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to ERROR),
                    warnings =
                        OutputChannel.of {
                            observedWarnings.add(it)
                        },
                )

            assertSolutionEquals(
                listOf(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            query.extractSignature(),
                        ),
                    ),
                ),
                solver.solve(query).toList(),
            )
            assertTrue { observedWarnings.isEmpty() }

            solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to WARNING),
                    warnings =
                        OutputChannel.of {
                            observedWarnings.add(it)
                        },
                )

            assertSolutionEquals(
                listOf(query.no()),
                solver.solve(query).toList(),
            )
            assertEquals(1, observedWarnings.size)
            assertTrue { observedWarnings[0] is MissingPredicate }
            assertEquals(query.extractSignature(), (observedWarnings[0] as MissingPredicate).signature)

            solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to FAIL),
                    warnings =
                        OutputChannel.of {
                            observedWarnings.add(it)
                        },
                )

            assertSolutionEquals(
                listOf(query.no()),
                solver.solve(query).toList(),
            )
            assertTrue { observedWarnings.size == 1 }
        }
    }

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
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = theory,
                    flags = FlagStore.of(Unknown to WARNING),
                    warnings =
                        OutputChannel.of {
                            observedWarnings.add(it)
                        },
                )

            val query = "ancestor"("abraham", X)

            val expectedSolutions =
                listOf(
                    query.yes(X to "isaac"),
                    query.yes(X to "jacob"),
                    query.yes(X to "joseph"),
                    query.no(),
                )

            assertSolutionEquals(expectedSolutions, solver.solve(query).toList())
            assertEquals(mutableListOf(), observedWarnings)

            solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = theory,
                    flags = FlagStore.of(Unknown to ERROR),
                    warnings =
                        OutputChannel.of {
                            observedWarnings.add(it)
                        },
                )

            assertSolutionEquals(expectedSolutions, solver.solve(query).toList())
            assertEquals(mutableListOf(), observedWarnings)

            solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = theory,
                    flags = FlagStore.of(Unknown to FAIL),
                    warnings =
                        OutputChannel.of {
                            observedWarnings.add(it)
                        },
                )

            assertSolutionEquals(expectedSolutions, solver.solve(query).toList())
            assertEquals(mutableListOf(), observedWarnings)
        }
    }

    // Test presence of correct built-ins
    // Test presence of correct built-ins
    scenario("solver/builtin-api") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            with(solver) {
                assertHasPredicateInAPI("!", 0)
                assertHasPredicateInAPI("call", 1)
                assertHasPredicateInAPI("catch", 3)
                assertHasPredicateInAPI("throw", 1)
                assertHasPredicateInAPI(",", 2)
                assertHasPredicateInAPI("\\+", 1)
                assertHasPredicateInAPI(Abolish)
                assertHasPredicateInAPI(Append.SIGNATURE)
                assertHasPredicateInAPI(Arg)
                assertHasPredicateInAPI(ArithmeticEqual)
                assertHasPredicateInAPI(ArithmeticGreaterThan)
                assertHasPredicateInAPI(ArithmeticGreaterThanOrEqualTo)
                assertHasPredicateInAPI(ArithmeticLowerThan)
                assertHasPredicateInAPI(ArithmeticLowerThanOrEqualTo)
                assertHasPredicateInAPI(ArithmeticNotEqual)
                assertHasPredicateInAPI(Arrow)
                assertHasPredicateInAPI(Assert)
                assertHasPredicateInAPI(AssertA)
                assertHasPredicateInAPI(AssertZ)
                assertHasPredicateInAPI(Atom)
                assertHasPredicateInAPI(AtomChars)
                assertHasPredicateInAPI(AtomCodes)
                assertHasPredicateInAPI(AtomConcat)
                assertHasPredicateInAPI(AtomLength)
                assertHasPredicateInAPI(Atomic)
                assertHasPredicateInAPI(BagOf)
                assertHasPredicateInAPI(Between)
                assertHasPredicateInAPI(Callable)
                assertHasPredicateInAPI(Clause)
                assertHasPredicateInAPI(Compound)
                assertHasPredicateInAPI(CopyTerm)
                assertHasPredicateInAPI(CurrentFlag)
                assertHasPredicateInAPI(CurrentOp)
                assertHasPredicateInAPI(CurrentPrologFlag)
                assertHasPredicateInAPI(EnsureExecutable)
                assertHasPredicateInAPI(FindAll)
                assertHasPredicateInAPI(FloatPrimitive)
                assertHasPredicateInAPI(Functor)
                assertHasPredicateInAPI(GetDurable)
                assertHasPredicateInAPI(GetEphemeral)
                assertHasPredicateInAPI(GetPersistent)
                assertHasPredicateInAPI(Ground)
                assertHasPredicateInAPI(Halt)
                assertHasPredicateInAPI(Halt1)
                assertHasPredicateInAPI(Integer)
                assertHasPredicateInAPI(Is)
                assertHasPredicateInAPI(Member.SIGNATURE)
                assertHasPredicateInAPI(Natural)
                assertHasPredicateInAPI(NewLine)
                assertHasPredicateInAPI(NonVar)
                assertHasPredicateInAPI(Not)
                assertHasPredicateInAPI(NotUnifiableWith)
                assertHasPredicateInAPI(Number)
                assertHasPredicateInAPI(NumberChars)
                assertHasPredicateInAPI(NumberCodes)
                assertHasPredicateInAPI(Once)
                assertHasPredicateInAPI(Op)
                assertHasPredicateInAPI(Repeat)
                assertHasPredicateInAPI(Retract)
                assertHasPredicateInAPI(RetractAll)
                assertHasPredicateInAPI(Reverse)
                assertHasPredicateInAPI(Semicolon.SIGNATURE)
                assertHasPredicateInAPI(SetDurable)
                assertHasPredicateInAPI(SetEphemeral)
                assertHasPredicateInAPI(SetFlag)
                assertHasPredicateInAPI(SetOf)
                assertHasPredicateInAPI(SetPersistent)
                assertHasPredicateInAPI(SetPrologFlag)
                assertHasPredicateInAPI(Sleep)
                assertHasPredicateInAPI(SubAtom)
                assertHasPredicateInAPI(TermGreaterThan)
                assertHasPredicateInAPI(TermGreaterThanOrEqualTo)
                assertHasPredicateInAPI(TermIdentical)
                assertHasPredicateInAPI(TermLowerThan)
                assertHasPredicateInAPI(TermLowerThanOrEqualTo)
                assertHasPredicateInAPI(TermNotIdentical)
                assertHasPredicateInAPI(TermNotSame)
                assertHasPredicateInAPI(TermSame)
                assertHasPredicateInAPI(UnifiesWith)
                assertHasPredicateInAPI(Univ)
                assertHasPredicateInAPI(Var)
                assertHasPredicateInAPI(Write)
            }
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
            val solver = solverFactory.solverWithDefaultBuiltins()

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

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            assertEquals(
                terms.map { it.format(TermFormatter.default()) }.append("\n"),
                outputs,
            )
        }
    }

    scenario("solver/standard-output") {
        val outputs = mutableListOf<String>()
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            with(solver.standardOutput) {
                addListener { outputs += it!! }
                write("a")
            }

            val query = write("b") and write("c") and write("d") and nl

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

            solver.standardOutput.write("e")

            assertEquals(
                listOf("a", "b", "c", "d", "\n", "e"),
                outputs,
            )
        }
    }

    scenario("solver/find-all") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theoryOf(
                            fact { "a"(1) },
                            fact { "a"(2) },
                            fact { "a"(3) },
                        ),
                )

            var query = findall(N, "a"(N), L)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes(L to logicListOf(1, 2, 3))),
                solutions,
            )

            query = findall(`_`, false, L)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes(L to emptyLogicList)),
                solutions,
            )

            query = findall(`_`, G, `_`)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("findall", 3),
                            variable = G,
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    scenario("solver/side-effects-persistent-after-backtracking1") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
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
            val solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(X to logicListOf(2, 3), Y to logicListOf(1)),
                    query.yes(X to logicListOf(3), Y to logicListOf(1, 2)),
                    query.yes(X to emptyLogicList, Y to logicListOf(1, 2, 3)),
                ),
                solutions,
            )
        }
    }

    // Test `true` goal

    case("solver/true") { truthOf(true) yields { listOf(it.yes()) } }

    // Test with [lessThan500MsGoalToSolution]
    // Test with [lessThan500MsGoalToSolution]
    scenario("solver/timeout1") {
        assertSolverSolutionsCorrect(
            solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = timeRelatedTheory,
                ),
            goalToSolutions = lessThan500MsGoalToSolution,
            maxDuration = 400L,
        )
    }

    // Test with [slightlyMoreThan500MsGoalToSolution]
    // Test with [slightlyMoreThan500MsGoalToSolution]
    scenario("solver/timeout2") {
        assertSolverSolutionsCorrect(
            solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = timeRelatedTheory,
                ),
            goalToSolutions = slightlyMoreThan500MsGoalToSolution,
            maxDuration = 599L,
        )
    }

    // Test with [slightlyMoreThan600MsGoalToSolution]
    // Test with [slightlyMoreThan600MsGoalToSolution]
    scenario("solver/timeout3") {
        assertSolverSolutionsCorrect(
            solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = timeRelatedTheory,
                ),
            goalToSolutions = slightlyMoreThan600MsGoalToSolution,
            maxDuration = 699L,
        )
    }

    // Test with [slightlyMoreThan700MsGoalToSolution]
    // Test with [slightlyMoreThan700MsGoalToSolution]
    scenario("solver/timeout4") {
        assertSolverSolutionsCorrect(
            solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = timeRelatedTheory,
                ),
            goalToSolutions = slightlyMoreThan700MsGoalToSolution,
            maxDuration = 799L,
        )
    }

    // Test with [ifThen1ToSolution]
    // Test with [ifThen1ToSolution]
    scenario("solver/if-then1") {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory1),
            goalToSolutions = ifThen1ToSolution,
            maxDuration = mediumDuration,
        )
    }

    // Test with [ifThenElse1ToSolution]
    // Test with [ifThenElse1ToSolution]
    scenario("solver/if-then-else1") {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory1),
            goalToSolutions = ifThenElse1ToSolution,
            maxDuration = mediumDuration,
        )
    }

    // Test with [ifThenElse2ToSolution]
    // Test with [ifThenElse2ToSolution]
    scenario("solver/if-then-else2") {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory2),
            goalToSolutions = ifThenElse2ToSolution,
            maxDuration = mediumDuration,
        )
    }

    // Test with [ifThen2ToSolution]
    // Test with [ifThen2ToSolution]
    scenario("solver/if-then2") {
        assertSolverSolutionsCorrect(
            solver = solverFactory.solverWithDefaultBuiltins(staticKb = ifThenTheory2),
            goalToSolutions = ifThen2ToSolution,
            maxDuration = mediumDuration,
        )
    }

    // Test with [simpleFactTheoryNotableGoalToSolutions]
    // Test with [simpleFactTheoryNotableGoalToSolutions]
    scenario("solver/unification") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleFactTheory),
            simpleFactTheoryNotableGoalToSolutions,
            mediumDuration,
        )
    }

    // Test with [simpleCutTheoryNotableGoalToSolutions]
    // Test with [simpleCutTheoryNotableGoalToSolutions]
    scenario("solver/simple-cut-alternatives") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleCutTheory),
            simpleCutTheoryNotableGoalToSolutions,
            mediumDuration,
        )
    }

    // Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions]
    // Test with [simpleCutAndConjunctionTheoryNotableGoalToSolutions]
    scenario("solver/cut-and-conjunction") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = simpleCutAndConjunctionTheory),
            simpleCutAndConjunctionTheoryNotableGoalToSolutions,
            mediumDuration,
        )
    }

    // Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions]
    // Test with [cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions]
    scenario("solver/cut-conjunction-and-backtracking") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = cutConjunctionAndBacktrackingTheory),
            cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
            mediumDuration,
        )
    }

    // Test with [infiniteComputationTheoryNotableGoalToSolution]
    // Test with [infiniteComputationTheoryNotableGoalToSolution]
    scenario("solver/max-duration-parameter-and-time-out-exception") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = infiniteComputationTheory),
            infiniteComputationTheoryNotableGoalToSolution,
            shortDuration,
        )
    }

    // Test with [prologStandardExampleTheoryNotableGoalToSolution]
    // Test with [prologStandardExampleTheoryNotableGoalToSolution]
    scenario("solver/prolog-standard-search-tree-example") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = prologStandardExampleTheory),
            prologStandardExampleTheoryNotableGoalToSolution,
            mediumDuration,
        )
    }

    // Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution]
    // Test with [prologStandardExampleWithCutTheoryNotableGoalToSolution]
    scenario("solver/prolog-standard-search-tree-with-cut-example") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(
                staticKb = PrologStandardExampleTheories.prologStandardExampleWithCutTheory,
            ),
            prologStandardExampleWithCutTheoryNotableGoalToSolution,
            mediumDuration,
        )
    }

    // Test with [customReverseListTheoryNotableGoalToSolution]
    // Test with [customReverseListTheoryNotableGoalToSolution]
    scenario("solver/backtracking-with-custom-reverse-list-implementation") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = customReverseListTheory),
            customReverseListTheoryNotableGoalToSolution,
            mediumDuration,
        )
    }

    // Test with [conjunctionStandardExampleTheoryNotableGoalToSolution]
    // Test with [conjunctionStandardExampleTheoryNotableGoalToSolution]
    scenario("solver/with-prolog-standard-conjunction-examples") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = conjunctionStandardExampleTheory),
            conjunctionStandardExampleTheoryNotableGoalToSolution,
            mediumDuration,
        )
    }

    // A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties
    // A test with all goals used in conjunction with `true` or `fail` to test Conjunction properties
    scenario("solver/conjunction-properties") {
        logicProgramming {
            val allDatabasesWithGoalsAndSolutions by lazy {
                allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
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
                assertSolverSolutionsCorrect(
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration,
                )
            }
        }
    }

    // Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution]
    // Call primitive testing with [callTestingGoalsToSolutions] and [callStandardExampleTheoryGoalsToSolution]
    scenario("solver/call-primitive") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = callStandardExampleTheory),
            callStandardExampleTheoryGoalsToSolution(callErrorSignature),
            mediumDuration,
        )

        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            callTestingGoalsToSolutions(callErrorSignature),
            mediumDuration,
        )
    }

    // A test in which all testing goals are called through the Call primitive
    // A test in which all testing goals are called through the Call primitive
    scenario("solver/call-primitive-transparency") {
        logicProgramming {
            allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
                callErrorSignature,
                nafErrorSignature,
                notErrorSignature,
            ).mapValues { (_, listOfGoalToSolutions) ->
                listOfGoalToSolutions.map { (goal, expectedSolutions) ->
                    call(goal).run { to(expectedSolutions.changeQueriesTo(this)) }
                }
            }.forEach { (database, goalToSolutions) ->
                assertSolverSolutionsCorrect(
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration,
                )
            }
        }
    }

    // Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution]
    // Call primitive testing with [catchTestingGoalsToSolutions] and [catchAndThrowTheoryExampleNotableGoalToSolution]
    scenario("solver/catch-primitive") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = catchAndThrowTheoryExample),
            catchAndThrowTheoryExampleNotableGoalToSolution,
            mediumDuration,
        )

        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            catchTestingGoalsToSolutions,
            mediumDuration,
        )
    }

    // A test in which all testing goals are called through the Catch primitive
    // A test in which all testing goals are called through the Catch primitive
    scenario("solver/catch-primitive-transparency") {
        logicProgramming {
            fun Struct.containsHaltPrimitive(): Boolean =
                when (functor) {
                    "halt" -> true
                    else -> argsSequence.filterIsInstance<Struct>().any { it.containsHaltPrimitive() }
                }

            allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
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
                assertSolverSolutionsCorrect(
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration,
                )
            }
        }
    }

    // Halt primitive testing with [haltTestingGoalsToSolutions]
    // Halt primitive testing with [haltTestingGoalsToSolutions]
    scenario("solver/halt-primitive") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            haltTestingGoalsToSolutions,
            mediumDuration,
        )
    }

    // Not rule testing with [notStandardExampleTheoryNotableGoalToSolution]
    // Not rule testing with [notStandardExampleTheoryNotableGoalToSolution]
    scenario("solver/not-primitive") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = notStandardExampleTheory),
            notStandardExampleTheoryNotableGoalToSolution(nafErrorSignature, notErrorSignature),
            mediumDuration,
        )
    }

    // A test in which all testing goals are called through the Not rule
    // A test in which all testing goals are called through the Not rule
    scenario("solver/not-modularity") {
        logicProgramming {
            allPrologTestingTheoriesToRespectiveGoalsAndSolutions(
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
                assertSolverSolutionsCorrect(
                    solverFactory.solverWithDefaultBuiltins(staticKb = database),
                    goalToSolutions,
                    mediumDuration,
                )
            }
        }
    }

    // If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution]
    // If-Then rule testing with [ifThenStandardExampleTheoryNotableGoalToSolution]
    scenario("solver/if-then-rule") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = ifThenStandardExampleTheory),
            ifThenStandardExampleTheoryNotableGoalToSolution,
            mediumDuration,
        )
    }

    // If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution]
    // If-Then-Else rule testing with [ifThenElseStandardExampleNotableGoalToSolution]
    scenario("solver/if-then-else-rule") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            ifThenElseStandardExampleNotableGoalToSolution,
            mediumDuration,
        )
    }

    // Test with [customRangeListGeneratorTheoryNotableGoalToSolution]
    // Test with [customRangeListGeneratorTheoryNotableGoalToSolution]
    scenario("solver/numbers-range-list-generation") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(staticKb = customRangeListGeneratorTheory),
            customRangeListGeneratorTheoryNotableGoalToSolution,
            mediumDuration,
        )
    }

    scenario("solver/failure") {
        // TODO: 12/11/2019 enrich this test after solving #51
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.no(),
                ),
                solutions,
            )
        }
    }

    scenario("solver/basic-backtracking1") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
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
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(N to 2),
                ),
                solutions,
            )
        }
    }

    scenario("solver/basic-backtracking2") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
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
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { listOf(yes(N to 3), yes(N to 2)) },
                solutions,
            )
        }
    }

    scenario("solver/basic-backtracking3") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
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
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(N to 2),
                ),
                solutions,
            )
        }
    }

    scenario("solver/basic-backtracking4") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
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
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(N to 2),
                ),
                solutions,
            )
        }
    }

    scenario("solver/conjunction") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theory(
                            { "a" impliedBy ("b" and "c") },
                            { "b" },
                            { "c" },
                        ),
                )
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(),
                ),
                solutions,
            )
        }
    }

    scenario("solver/conjunction-of-conjunctions") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
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
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(),
                ),
                solutions,
            )
        }
    }

    scenario("solver/conjunction-with-unification") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theory(
                            { "a"(X) impliedBy ("b"(X) and "c"(X)) },
                            { "b"(1) },
                            { "c"(1) },
                        ),
                )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(N to 1),
                ),
                solutions,
            )
        }
    }

    scenario("solver/disjunction") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theory(
                            { "a" impliedBy ("b" or "c") },
                            { "b" },
                            { "c" },
                        ),
                )
            val query = atomOf("a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { listOf(yes(), yes()) },
                solutions,
            )
        }
    }

    scenario("solver/disjunction-with-unification") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theory(
                            { "a"(X) impliedBy ("b"(X) or "c"(X)) },
                            { "b"(1) },
                            { "c"(2) },
                        ),
                )
            val query = "a"(N)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) { listOf(yes(N to 1), yes(N to 2)) },
                solutions,
            )

            assertEquals(2, solutions.size)
        }
    }

    scenario("solver/member") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            assertSolverSolutionsCorrect(solver, memberGoalToSolution, mediumDuration)
        }
    }

    scenario("solver/assert-rules") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = assertz("f"(2) impliedBy false) and asserta("f"(1) impliedBy true)

            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )

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
                solverFactory.solverWithDefaultBuiltins(
                    dynamicKb =
                        theoryOf(
                            factOf(structOf("f", numOf(1))),
                            ruleOf(structOf("f", numOf(2)), atomOf("false")),
                        ),
                )

            val query = retract("f"(X)) // retract(f(X))

            val solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(X to 1),
                    query.yes(X to 2),
                ),
                solutions,
            )

            assertEquals(
                listOf(),
                solver.dynamicKb.toList(),
            )
            assertEquals(0L, solver.dynamicKb.size)
        }
    }

    scenario("solver/natural") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = natural(X) and natural(X)

            val n = 100

            val solutions = solver.solve(query, mediumDuration).take(n).toList()

            assertSolutionEquals(
                (0 until n).map { query.yes(X to it) },
                solutions,
            )
        }
    }

    case("solver/functor-1") { functor("a"("b", "c"), X, Y) yields { listOf(it.yes(X to "a", Y to 2)) } }
    case("solver/functor-2") { functor("a"("b", "c"), "a", Y) yields { listOf(it.yes(Y to 2)) } }
    case("solver/functor-3") { functor("a"("b", "c"), X, 2) yields { listOf(it.yes(X to "a")) } }

    case("solver/functor-4") {
        functor(X, "a", 2) yields {
            listOf(it.yes(X to structOf("a", anonymous(), anonymous())))
        }
    }

    case("solver/functor-5") {
        functor(X, Y, 2) yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        variable = Y,
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("solver/functor-6") {
        functor(X, "a", "2") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.INTEGER,
                        atomOf("2"),
                        2,
                    ),
                ),
            )
        }
    }

    case("solver/univ-1") { "a"("b", "c") univ X yields { listOf(it.yes(X to logicListOf("a", "b", "c"))) } }

    case("solver/univ-2") {
        X univ logicListOf("a", "b", "c") yields {
            listOf(it.yes(X to structOf("a", "b", "c")))
        }
    }

    case("solver/univ-3") {
        X univ Y yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=..", 2),
                        variable = X,
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("solver/univ-4") {
        "a"("b", "c") univ "a" yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("=..", 2),
                        TypeError.Expected.LIST,
                        atomOf("a"),
                        1,
                    ),
                ),
            )
        }
    }

    case("solver/append-1") {
        append(logicListOf(1, 2, 3), logicListOf(4, 5, 6), X) yields {
            listOf(it.yes(X to logicListOf(1, 2, 3, 4, 5, 6)))
        }
    }

    case("solver/append-2") {
        append(logicListOf(1, 2, 3), X, logicListOf(1, 2, 3, 4, 5, 6)) yields {
            listOf(it.yes(X to logicListOf(4, 5, 6)))
        }
    }

    case("solver/append-3") { append(X, X, logicListOf(1, 2, 3, 4, 5, 6)) yields { listOf(it.no()) } }

    case("solver/append-4") {
        append(X, Y, logicListOf(1, 2, 3, 4, 5, 6)) yields {
            listOf(
                it.yes(X to emptyLogicList, Y to logicListOf(1, 2, 3, 4, 5, 6)),
                it.yes(X to logicListOf(1), Y to logicListOf(2, 3, 4, 5, 6)),
                it.yes(X to logicListOf(1, 2), Y to logicListOf(3, 4, 5, 6)),
                it.yes(X to logicListOf(1, 2, 3), Y to logicListOf(4, 5, 6)),
                it.yes(X to logicListOf(1, 2, 3, 4), Y to logicListOf(5, 6)),
                it.yes(X to logicListOf(1, 2, 3, 4, 5), Y to logicListOf(6)),
                it.yes(X to logicListOf(1, 2, 3, 4, 5, 6), Y to emptyLogicList),
            )
        }
    }

    scenario("solver/retract-all") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    dynamicKb =
                        theoryOf(
                            factOf(structOf("f", numOf(1))),
                            ruleOf(structOf("f", numOf(2)), atomOf("false")),
                        ),
                )

            var query = retractall("f"(X))

            var solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(),
                ),
                solutions,
            )

            assertEquals(
                listOf(),
                solver.dynamicKb.toList(),
            )
            assertEquals(0L, solver.dynamicKb.size)

            query = retractall("f"(X))
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(),
                ),
                solutions,
            )
        }
    }

    scenario("solver/term-greater-than") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            greaterThanTesting,
            mediumDuration,
        )
    }

    scenario("solver/term-greater-than-or-equal") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            greaterThanOrEqualTesting,
            mediumDuration,
        )
    }

    scenario("solver/term-same") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            equalTesting,
            mediumDuration,
        )
    }

    scenario("solver/term-not-same") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            notEqualTesting,
            mediumDuration,
        )
    }

    scenario("solver/term-lower-than") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            lowerThanTesting,
            mediumDuration,
        )
    }

    scenario("solver/term-lower-than-or-equal") {
        assertSolverSolutionsCorrect(
            solverFactory.solverWithDefaultBuiltins(),
            lowerThanOrEqualTesting,
            mediumDuration,
        )
    }
}

private fun ContractScope.testAssert(
    suffix: String,
    inverse: Boolean,
) {
    logicProgramming {
        val solver = solverFactory.solverWithDefaultBuiltins()
        val assertX = "assert$suffix"

        val query =
            assertX("f"(1)) and
                assertX("f"(2)) and
                assertX("f"(3)) and
                "f"(X)

        val solutions = solver.solve(query, mediumDuration).toList()
        val ints = if (inverse) (3 downTo 1) else (1..3)

        assertSolutionEquals(
            (ints).map { query.yes(X to it) },
            solutions,
        )

        ints.forEach {
            assertTrue {
                "f"(it) in solver.dynamicKb
            }
        }
    }
}
