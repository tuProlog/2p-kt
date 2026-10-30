@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier.XFX
import it.unibo.tuprolog.core.operators.Specifier.XFY
import it.unibo.tuprolog.core.operators.Specifier.YFX
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DirectiveTestsUtils.bigTheory
import it.unibo.tuprolog.solve.DirectiveTestsUtils.dynamicDirective
import it.unibo.tuprolog.solve.DirectiveTestsUtils.facts
import it.unibo.tuprolog.solve.DirectiveTestsUtils.solverInitializers
import it.unibo.tuprolog.solve.DirectiveTestsUtils.solverInitializersWithEventsList
import it.unibo.tuprolog.solve.DirectiveTestsUtils.staticDirective
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.warning.InitializationIssue
import it.unibo.tuprolog.theory.Theory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Conformance tests for directive clauses (`:- Goal.`), covering `dynamic/1`, `static/1`, `initialization/1`, `solve/1`, `set_flag/2`, `set_prolog_flag/2`, `op/3`, malformed directives, and directives that fail or raise during theory loading. */
fun directivesContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests that a theory containing
    // ```prolog
    // f(1). f(2). f(3).
    // :- static(g/1).
    // g(4). g(5). g(6).
    // :- dynamic(h/1).
    // h(7). h(8). h(9).
    // ```
    // loaded as a solver's static knowledge base ends up with `f/1` and `g/1`'s facts (the ones before the first
    // `dynamic/1` directive) in the static theory, and `h/1`'s facts in the dynamic theory; and that loading the
    // same theory into a fresh `MutableSolver` via `loadStaticKb` produces the same split.
    scenario("directives/dynamic1") {
        logicProgramming {
            val initialStaticKb =
                theoryOf(
                    facts("f", 1..3),
                    staticDirective("g", 1),
                    facts("g", 4..6),
                    dynamicDirective("h", 1),
                    facts("h", 7..9),
                )

            val expectedStatic = theoryOf(initialStaticKb.take(8))
            val expectedDynamic = mutableTheoryOf(initialStaticKb.drop(8))

            val solver = solverFactory.solverOf(staticKb = initialStaticKb)

            assertEquals(expectedStatic, solver.staticKb)
            assertEquals(expectedDynamic, solver.dynamicKb)

            val mutableSolver = solverFactory.mutableSolverOf()

            assertEquals(emptyTheory(), mutableSolver.staticKb)
            assertEquals(emptyMutableTheory(), mutableSolver.dynamicKb)

            mutableSolver.loadStaticKb(initialStaticKb)

            assertEquals(expectedStatic, mutableSolver.staticKb)
            assertEquals(expectedDynamic, mutableSolver.dynamicKb)
        }
    }

    // Tests that a theory with the same shape as in [testDynamic1] but loaded as a solver's *dynamic* knowledge
    // base ends up with `g/1`'s facts (following the `static/1` directive) moved to the static theory, and every
    // other fact (`f/1` and `h/1`) remaining in the dynamic theory; and that loading it into a fresh `MutableSolver`
    // via `loadDynamicKb` produces the same split.
    scenario("directives/static1") {
        logicProgramming {
            val initialDynamicKb =
                theoryOf(
                    facts("f", 1..3),
                    staticDirective("g", 1),
                    facts("g", 4..6),
                    dynamicDirective("h", 1),
                    facts("h", 7..9),
                )

            val expectedStatic = theoryOf(initialDynamicKb.drop(4).take(3))
            val expectedDynamic =
                theoryOf(
                    facts("f", 1..3),
                    staticDirective("g", 1),
                    dynamicDirective("h", 1),
                    facts("h", 7..9),
                ).toMutableTheory()

            val solver = solverFactory.solverOf(staticKb = emptyTheory(), dynamicKb = initialDynamicKb)

            assertEquals(expectedStatic, solver.staticKb)
            assertEquals(expectedDynamic, solver.dynamicKb)

            val mutableSolver = solverFactory.mutableSolverOf()

            assertEquals(emptyTheory(), mutableSolver.staticKb)
            assertEquals(emptyMutableTheory(), mutableSolver.dynamicKb)

            mutableSolver.loadDynamicKb(initialDynamicKb)

            assertEquals(expectedStatic, mutableSolver.staticKb)
            assertEquals(expectedDynamic, mutableSolver.dynamicKb)
        }
    }

    // Tests that, for a theory containing an `initialization/1` directive whose goal writes to the standard output
    // and calls another predicate that also writes, loading the theory (via any of
    // [DirectiveTestsUtils.solverInitializers]) triggers those writes, in clause order, exactly once each, and that
    // the predicate defined after the last `initialization/1` directive remains solvable afterwards.
    scenario("directives/initialization1") {
        testInit("initialization")
    }

    // Same as [testInitialization1], but for the `solve/1` directive instead of `initialization/1`.
    scenario("directives/solve1") {
        testInit("solve")
    }

    // Tests that a theory mixing malformed directives (`op("a", xfx, "++")` with a non-integer priority,
    // `op(3, "b", "+++")` with a non-atom specifier, `set_flag(a, x)` with an inadmissible value, and misspelled
    // `dinamic/1`/`statyc/1` directives) with well-formed facts still loads without emitting any writing/warning
    // event, and that the facts end up in the theory (static or dynamic, depending on how it was loaded).
    scenario("directives/wrong-directives") {
        logicProgramming {
            for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
                val theory =
                    theoryOf(
                        directive { op("a", XFX, "++") },
                        directive { op(3, "b", "+++") },
                        directive { "set_flag"("a", "x") },
                        directive { "dinamic"("f" / 1) },
                        fact { "f"("a") },
                        directive { "statyc"("g" / 1) },
                        fact { "g"("b") },
                        directive { "init"(write("something")) },
                    )
                val solver = solverOf(theory)

                assertEquals(
                    listOf<Any>(),
                    events,
                )

                sequenceOf(solver.staticKb, solver.dynamicKb)
                    .single { it.size > 0 }
                    .let { assertEquals(theory, it) }
            }
        }
    }

    // Tests that a theory containing
    // ```prolog
    // :- set_flag(a, 1).
    // :- set_flag(b, 2).
    // :- set_flag(c, 3).
    // ```
    // loaded via any of [DirectiveTestsUtils.solverInitializers] results in a solver whose `flags` map contains `a`,
    // `b` and `c` bound to `1`, `2` and `3` respectively, alongside the default flags.
    scenario("directives/set-flag2") {
        logicProgramming {
            for (solverOf in solverInitializers(solverFactory)) {
                val solver =
                    solverOf(
                        theoryOf(
                            directive { set_flag("a", 1) },
                            directive { set_flag("b", 2) },
                            directive { set_flag("c", 3) },
                        ),
                    )

                assertTrue { solver.flags.size >= 3 }
                assertEquals(Integer.of(1), solver.flags["a"])
                assertEquals(Integer.of(2), solver.flags["b"])
                assertEquals(Integer.of(3), solver.flags["c"])
            }
        }
    }

    // Same as [testSetFlag2], but using the `set_prolog_flag/2` directive instead of `set_flag/2`.
    scenario("directives/set-prolog-flag2") {
        logicProgramming {
            for (solverOf in solverInitializers(solverFactory)) {
                val solver =
                    solverOf(
                        theoryOf(
                            directive { set_prolog_flag("a", 1) },
                            directive { set_prolog_flag("b", 2) },
                            directive { set_prolog_flag("c", 3) },
                        ),
                    )

                assertTrue { solver.flags.size >= 3 }
                assertEquals(Integer.of(1), solver.flags["a"])
                assertEquals(Integer.of(2), solver.flags["b"])
                assertEquals(Integer.of(3), solver.flags["c"])
            }
        }
    }

    // Tests that a theory containing
    // ```prolog
    // :- op(2, xfx, '++').
    // :- op(3, xfy, '+++').
    // :- op(4, yfx, '++++').
    // ```
    // loaded via any of [DirectiveTestsUtils.solverInitializers] results in a solver whose `operators` set contains
    // all three newly-declared operators, with the declared priority and specifier.
    scenario("directives/op3") {
        logicProgramming {
            for (solverOf in solverInitializers(solverFactory)) {
                val solver =
                    solverOf(
                        theoryOf(
                            directive { op(2, XFX, "++") },
                            directive { op(3, XFY, "+++") },
                            directive { op(4, YFX, "++++") },
                        ),
                    )

                val expectedOperators =
                    OperatorSet(
                        Operator("++++", YFX, 4),
                        Operator("+++", XFY, 3),
                        Operator("++", XFX, 2),
                    )

                for (operator in expectedOperators) {
                    assertTrue { operator in solver.operators }
                }
            }
        }
    }

    // Tests that a theory with a single `initialization/1` directive whose goal always fails still loads (the fact
    // ends up in the appropriate theory), and that exactly one `InitializationIssue` warning event, mentioning
    // "failure", is emitted.
    scenario("directives/failing-initialization1") {
        testFailingInit("initialization")
    }

    // Same as [testFailingInitialization1], but for the `solve/1` directive instead of `initialization/1`.
    scenario("directives/failing-solve1") {
        testFailingInit("solve")
    }

    // Tests that a theory with a single `initialization/1` directive whose goal raises an [it.unibo.tuprolog.solve.exception.error.InstantiationError]
    // (an unbound variable used in an arithmetic expression) still loads, and that exactly one `InitializationIssue`
    // warning event, mentioning the raised error, is emitted.
    scenario("directives/exceptional-initialization1") {
        testExceptionalInit("initialization")
    }

    // Same as [testExceptionalInitialization1], but for the `solve/1` directive instead of `initialization/1`.
    scenario("directives/exceptional-solve1") {
        testExceptionalInit("solve")
    }

    // Tests that loading, with default built-ins, a theory of [DirectiveTestsUtils.bigTheory]'s default size
    // (40000 facts) completes at all within this suite's test timeout — a basic guard against quadratic-or-worse
    // loading time regressions. Unlike the other test cases here, it does not inspect the resulting solver.
    scenario("directives/directive-loading-quickly") {
        solverFactory.solverWithDefaultBuiltins(staticKb = bigTheory())
    }
}

private fun theoryWithInit(initGoal: String): Theory =
    logicProgramming {
        theoryOf(
            directive { initGoal(write("a")) },
            fact { "f"(1) },
            directive { initGoal(tupleOf("f"(X), write("f"(X)))) },
            fact { "f"(2) },
            directive { initGoal(write("b")) },
        )
    }

private fun ContractScope.testInit(initGoal: String) =
    logicProgramming {
        val theory = theoryWithInit(initGoal)
        for ((solverOf, writeEvents) in solverInitializersWithEventsList(solverFactory)) {
            val solver = solverOf(theory)

            assertEquals(
                listOf<Any>("a", "f(1)", "f(2)", "b"),
                writeEvents,
            )

            val query = "f"(X)
            val expectedSolutions =
                listOf(
                    query.yes(X to 1),
                    query.yes(X to 2),
                )

            assertEquals(
                expectedSolutions,
                solver.solve(query).toList(),
            )
        }
    }

private fun theoryWithFailingInit(initGoal: String): Theory =
    logicProgramming {
        theoryOf(directive { initGoal(fail) })
    }

private fun ContractScope.testFailingInit(initGoal: String) =
    logicProgramming {
        val theory = theoryWithFailingInit(initGoal)
        for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
            val solver = solverOf(theory)

            sequenceOf(solver.staticKb, solver.dynamicKb)
                .single { it.size > 0 }
                .let { assertEquals(theory, it) }

            assertTrue { events.size == 1 }
            assertTrue { events[0] is InitializationIssue }
            assertTrue { (events[0] as? InitializationIssue)?.message?.contains("failure") ?: false }
        }
    }

private fun theoryWithExceptionalInit(initGoal: String): Theory =
    logicProgramming {
        theoryOf(directive { initGoal(X `is` (Y + 1)) })
    }

private fun ContractScope.testExceptionalInit(initGoal: String) =
    logicProgramming {
        val theory = theoryWithExceptionalInit(initGoal)
        for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
            val solver = solverOf(theory)

            sequenceOf(solver.staticKb, solver.dynamicKb)
                .single { it.size > 0 }
                .let { assertEquals(theory, it) }

            assertTrue { events.size == 1 }
            assertTrue { events[0] is InitializationIssue }
            assertTrue {
                (events[0] as? InitializationIssue)?.message?.contains(InstantiationError.typeFunctor) ?: false
            }
        }
    }
