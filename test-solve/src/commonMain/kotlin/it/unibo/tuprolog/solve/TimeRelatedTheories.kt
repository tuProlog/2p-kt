package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.theory.Theory

/**
 * A theory whose solutions each take a known amount of time (via `sleep/1`), and the expected [Solution]s of
 * resolving it with a per-solution timeout (see [maxDurationFor]) that lets exactly 0, 1, 2 or 3 of them arrive in
 * time; used by [solverContract] to check that a `Solver.solve(goal, maxDuration)` call's timeout is honored.
 */
object TimeRelatedTheories {
    internal val timeOutException = TimeOutException(context = DummyInstances.executionContext, exceededDuration = 1)

    /** The k-th solution of `a(X)` busy-waits `k * SLEEP_STEP` ms, so solution times are this far apart. */
    const val SLEEP_STEP = 500L

    /**
     * A timeout that lets exactly [solutions] solutions of `a(X)` arrive in time. The timeout restarts with each
     * solution, so the margin left to the last expected solution is `SLEEP_STEP - 100` ms. That margin absorbs a slow
     * or busy machine: running late can only turn an expected solution into a timeout, never the other way round.
     */
    @Suppress("MagicNumber")
    fun maxDurationFor(solutions: Int): Long = SLEEP_STEP * (solutions + 1) - 100

    /**
     * Clause database used for time-related tests:
     *
     * ```prolog
     * a(X) :- b(X).
     * b(500) :- sleep(500).
     * b(1000) :- sleep(1000).
     * b(1500) :- sleep(1500).
     * ```
     */
    val timeRelatedTheory: Theory by lazy {
        logicProgramming {
            theoryOf(
                rule { "a"("X") `if` "b"("X") },
                *(1..3).map { k -> rule { "b"(SLEEP_STEP * k) `if` "sleep"(SLEEP_STEP * k) } }.toTypedArray(),
            )
        }
    }

    /** `a(X)` with [maxDurationFor] `(0)`: the first solution is already a [TimeOutException]. */
    val noSolutionInTimeGoalToSolution by lazy { inTimeGoalToSolution(0) }

    /** `a(X)` with [maxDurationFor] `(1)`: one solution, then a [TimeOutException]. */
    val oneSolutionInTimeGoalToSolution by lazy { inTimeGoalToSolution(1) }

    /** `a(X)` with [maxDurationFor] `(2)`: two solutions, then a [TimeOutException]. */
    val twoSolutionsInTimeGoalToSolution by lazy { inTimeGoalToSolution(2) }

    /** `a(X)` with [maxDurationFor] `(3)`: all three solutions, no [TimeOutException]. */
    val allSolutionsInTimeGoalToSolution by lazy { inTimeGoalToSolution(3) }

    private fun inTimeGoalToSolution(solutions: Int): List<Pair<Struct, List<Solution>>> {
        val x = Var.of("X")
        val query = Struct.of("a", x)
        val inTime = (1..solutions).map { k -> query.yes(Substitution.of(x, Integer.of(SLEEP_STEP * k))) }
        val timedOut = if (solutions < 3) listOf(query.halt(timeOutException)) else emptyList()
        return listOf(query to inTime + timedOut)
    }
}
