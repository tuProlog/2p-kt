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

@Suppress("DEPRECATION")
class TestDirectivesImpl(private val solverFactory: SolverFactory) : TestDirectives {

    override fun testDynamic1() {
        logicProgramming {
            val initialStaticKb = theoryOf(
                facts("f", 1..3),
                staticDirective("g", 1),
                facts("g", 4..6),
                dynamicDirective("h", 1),
                facts("h", 7..9)
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

    override fun testStatic1() {
        logicProgramming {
            val initialDynamicKb = theoryOf(
                facts("f", 1..3),
                staticDirective("g", 1),
                facts("g", 4..6),
                dynamicDirective("h", 1),
                facts("h", 7..9)
            )

            val expectedStatic = theoryOf(initialDynamicKb.drop(4).take(3))
            val expectedDynamic = theoryOf(
                facts("f", 1..3),
                staticDirective("g", 1),
                dynamicDirective("h", 1),
                facts("h", 7..9)
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

    private fun theoryWithInit(initGoal: String): Theory = logicProgramming {
        theoryOf(
            directive { initGoal(write("a")) },
            fact { "f"(1) },
            directive { initGoal(tupleOf("f"(X), write("f"(X)))) },
            fact { "f"(2) },
            directive { initGoal(write("b")) },
        )
    }

    private fun testInit(initGoal: String) =
        logicProgramming {
            val theory = theoryWithInit(initGoal)
            for ((solverOf, writeEvents) in solverInitializersWithEventsList(solverFactory)) {
                val solver = solverOf(theory)

                assertEquals(
                    ktListOf<Any>("a", "f(1)", "f(2)", "b"),
                    writeEvents
                )

                val query = "f"(X)
                val expectedSolutions = ktListOf(
                    query.yes(X to 1),
                    query.yes(X to 2)
                )

                assertEquals(
                    expectedSolutions,
                    solver.solve(query).toList()
                )
            }
        }

    override fun testInitialization1() = testInit("initialization")

    override fun testSolve1() = testInit("solve")

    override fun testSetFlag2() {
        logicProgramming {
            for (solverOf in solverInitializers(solverFactory)) {
                val solver = solverOf(
                    theoryOf(
                        directive { set_flag("a", 1) },
                        directive { set_flag("b", 2) },
                        directive { set_flag("c", 3) },
                    )
                )

                assertTrue { solver.flags.size >= 3 }
                assertEquals(Integer.of(1), solver.flags["a"])
                assertEquals(Integer.of(2), solver.flags["b"])
                assertEquals(Integer.of(3), solver.flags["c"])
            }
        }
    }

    override fun testSetPrologFlag2() {
        logicProgramming {
            for (solverOf in solverInitializers(solverFactory)) {
                val solver = solverOf(
                    theoryOf(
                        directive { set_prolog_flag("a", 1) },
                        directive { set_prolog_flag("b", 2) },
                        directive { set_prolog_flag("c", 3) },
                    )
                )

                assertTrue { solver.flags.size >= 3 }
                assertEquals(Integer.of(1), solver.flags["a"])
                assertEquals(Integer.of(2), solver.flags["b"])
                assertEquals(Integer.of(3), solver.flags["c"])
            }
        }
    }

    override fun testOp3() {
        logicProgramming {
            for (solverOf in solverInitializers(solverFactory)) {
                val solver = solverOf(
                    theoryOf(
                        directive { op(2, XFX, "++") },
                        directive { op(3, XFY, "+++") },
                        directive { op(4, YFX, "++++") },
                    )
                )

                val expectedOperators = OperatorSet(
                    Operator("++++", YFX, 4),
                    Operator("+++", XFY, 3),
                    Operator("++", XFX, 2)
                )

                for (operator in expectedOperators) {
                    assertTrue { operator in solver.operators }
                }
            }
        }
    }

    override fun testWrongDirectives() {
        logicProgramming {
            for ((solverOf, events) in solverInitializersWithEventsList(solverFactory)) {
                val theory = theoryOf(
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
                    ktListOf<Any>(),
                    events
                )

                sequenceOf(solver.staticKb, solver.dynamicKb)
                    .single { it.size > 0 }
                    .let { assertEquals(theory, it) }
            }
        }
    }

    private fun theoryWithFailingInit(initGoal: String): Theory = logicProgramming {
        theoryOf(directive { initGoal(fail) })
    }

    private fun testFailingInit(initGoal: String) =
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

    override fun testFailingInitialization1() {
        testFailingInit("initialization")
    }

    override fun testFailingSolve1() {
        testFailingInit("solve")
    }

    private fun theoryWithExceptionalInit(initGoal: String): Theory = logicProgramming {
        theoryOf(directive { initGoal(X `is` (Y + 1)) })
    }

    private fun testExceptionalInit(initGoal: String) =
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

    override fun testExceptionalInitialization1() {
        testExceptionalInit("initialization")
    }

    override fun testExceptionalSolve1() {
        testExceptionalInit("solve")
    }

    override fun testDirectiveLoadingQuickly() {
        solverFactory.solverWithDefaultBuiltins(staticKb = bigTheory())
    }
}
