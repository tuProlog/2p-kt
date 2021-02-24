package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.channel.OutputChannel
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestRecursionImpl(private val solverFactory: SolverFactory) : TestRecursion {

    private fun thermostat(initialTemp: Int, goodRange: IntRange) = prolog {
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
                    "check_temperature"
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
                    "check_temperature"
                )
            },
            rule {
                "check_temperature".impliedBy(
                    "temp"(X),
                    X greaterThan goodRange.first,
                    X lowerThan goodRange.last,
                    cut,
                    write(X)
                )
            },
            rule {
                "change_temperature"(X).impliedBy(
                    retract("temp"(`_`)),
                    assert("temp"(X))
                )
            }
        )
    }

    private fun testRecursion(init: Int, range: IntRange) {
        prolog {
            val prints = mutableListOf<String>()
            val solver = solverFactory.mutableSolverWithDefaultBuiltins(
                staticKb = thermostat(init, range),
                stdOut = OutputChannel.of { prints.add(it) }
            )

            val sol = solver.solveOnce(atomOf("check_temperature"), mediumDuration)

            assertTrue { sol.isYes }

            if (init <= range.first) {
                assertEquals(
                    (init..range.first + 1).map { it.toString() },
                    prints
                )
            } else if (init >= range.last) {
                assertEquals(
                    (range.last - 1..init).toList().asReversed().map { it.toString() },
                    prints
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
}
