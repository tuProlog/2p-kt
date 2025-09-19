package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.LastCallOptimization
import it.unibo.tuprolog.solve.flags.LastCallOptimization.OFF
import it.unibo.tuprolog.solve.flags.LastCallOptimization.ON
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestRecursionImpl(
    private val solverFactory: SolverFactory,
) : TestRecursion {
    override val shortDuration: TimeDuration
        get() = 1000

    private fun thermostat(
        initialTemp: Int,
        goodRange: IntRange,
    ) = logicProgramming {
        theoryOf(
            directive { dynamic("temp" / 1) },
            fact { "temp"(initialTemp) },
            rule {
                "check_temperature".impliedBy(
                    "temp"(X),
                    X greaterThanOrEqualsTo goodRange.last,
                    cut,
                    write(X),
                    T `is` (X - 1),
                    "change_temperature"(T),
                    "check_temperature",
                )
            },
            rule {
                "check_temperature".impliedBy(
                    "temp"(X),
                    X lowerThanOrEqualsTo goodRange.first,
                    cut,
                    write(X),
                    T `is` (X + 1),
                    "change_temperature"(T),
                    "check_temperature",
                )
            },
            rule {
                "check_temperature".impliedBy(
                    "temp"(X),
                    X greaterThan goodRange.first,
                    X lowerThan goodRange.last,
                    cut,
                    write(X),
                )
            },
            rule {
                "change_temperature"(X).impliedBy(
                    retract("temp"(`_`)),
                    assert("temp"(X)),
                )
            },
        )
    }

    private fun testRecursion(
        init: Int,
        range: IntRange,
    ) {
        logicProgramming {
            val prints = mutableListOf<String>()
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = thermostat(init, range),
                    stdOut = OutputChannel.of { prints.add(it) },
                )

            val sol = solver.solveOnce(atomOf("check_temperature"), mediumDuration)

            assertTrue { sol.isYes }

            if (init <= range.first) {
                assertEquals(
                    (init..range.first + 1).map { it.toString() },
                    prints,
                )
            } else if (init >= range.last) {
                assertEquals(
                    (range.last - 1..init).toList().asReversed().map { it.toString() },
                    prints,
                )
            }
        }
    }

    override fun testRecursion1() {
        testRecursion(10, 18..22)
    }

    override fun testRecursion2() {
        testRecursion(30, 18..22)
    }

    private fun canary(message: String) =
        logicProgramming {
            theoryOf(
                rule { "recursive"(0) impliedBy `throw`(message) },
                rule {
                    "recursive"(N).impliedBy(
                        N greaterThan 0,
                        M `is` (N - 1),
                        "recursive"(M),
                    )
                },
            )
        }

    private fun testTailRecursion(
        lastCallOptimization: Boolean,
        n: Int = 100,
    ) {
        logicProgramming {
            val prints = mutableListOf<String>()
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb = canary("ball"),
                    flags = FlagStore.DEFAULT.set(LastCallOptimization, if (lastCallOptimization) ON else OFF),
                    stdOut = OutputChannel.of { prints.add(it) },
                )

            assertEquals(if (lastCallOptimization) ON else OFF, solver.flags[LastCallOptimization])

            val query = "recursive"(n)

            val sol = solver.solveOnce(query, longDuration)

            assertSolutionEquals(
                query.halt(
                    SystemError.forUncaughtException(
                        DummyInstances.executionContext,
                        atomOf("ball"),
                    ),
                ),
                sol,
            )
            if (lastCallOptimization) {
                assertEquals(2, (sol as Solution.Halt).exception.logicStackTrace.size)
            } else {
                assertEquals(n + 2, (sol as Solution.Halt).exception.logicStackTrace.size)
            }
        }
    }

    override fun testTailRecursion() {
        testTailRecursion(true)
    }

    override fun testNonTailRecursion() {
        testTailRecursion(false)
    }
}
