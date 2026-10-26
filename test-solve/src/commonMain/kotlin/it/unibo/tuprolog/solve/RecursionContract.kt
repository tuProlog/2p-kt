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

/** Conformance tests exercising recursive rules, both to check that recursion (with backtracking-driven mutable state, and deep tail recursion) actually resolves correctly, and that [it.unibo.tuprolog.solve.flags.LastCallOptimization] measurably affects how deep the logic stack trace grows on a failing recursive computation. */
fun recursionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests a hand-written thermostat-like theory (a mutable `temp/1` fact adjusted one degree at a time via
    // `retract/1`+`assert/1`, driven by a recursive `check_temperature/0` rule) starting at `10` and converging,
    // printing each intermediate temperature, up to the low end (`18`) of the target range `18..22`.
    scenario("recursion/recursion1") {
        testRecursion(10, 18..22)
    }

    // Same theory as [testRecursion1], but starting at `30`, converging down to the high end (`22`) of the range.
    scenario("recursion/recursion2") {
        testRecursion(30, 18..22)
    }

    // Tests that, with [it.unibo.tuprolog.solve.flags.LastCallOptimization] `on`, a 100-deep tail-recursive `recursive/1`
    // predicate that eventually throws leaves only 2 frames (the throwing goal and the top-level query) in the
    // resulting [Solution.Halt]'s logic stack trace, since tail calls do not accumulate stack frames.
    scenario("recursion/tail-recursion") {
        testTailRecursion(true)
    }

    // Same recursive theory as [testTailRecursion], but with [it.unibo.tuprolog.solve.flags.LastCallOptimization]
    // `off`: the logic stack trace grows by one frame per recursive call, ending up `n + 2` frames deep.
    scenario("recursion/non-tail-recursion") {
        testTailRecursion(false)
    }
}

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

private fun ContractScope.testRecursion(
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

private fun ContractScope.testTailRecursion(
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
