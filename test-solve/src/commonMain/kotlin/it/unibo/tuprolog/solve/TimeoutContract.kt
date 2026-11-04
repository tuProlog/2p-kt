package it.unibo.tuprolog.solve

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.stdlib.primitive.FindAll
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.time.ChronometerFactory
import it.unibo.tuprolog.utils.time.ManualTimeline
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/** The time budget of the simulated-time scenarios. */
private val BUDGET = 20.milliseconds

/** The virtual cost of each resolution step, in leap-driven scenarios. */
private val LEAP = 1.milliseconds

/**
 * Resolution on simulated time (see [ChronometerFactory.simulated]): a fresh virtual [timeline], where each
 * resolution step costs [durationPerLeap] and `sleep/1` advances time without waiting, so outcomes are
 * deterministic and immediate, whatever the host machine.
 */
private class SimulatedTime(
    timeout: Duration = BUDGET,
    durationPerLeap: Duration = Duration.ZERO,
) {
    val timeline: ManualTimeline = Timeline.manual()

    val options: SolveOptions =
        SolveOptions
            .allLazilyWithTimeout(timeout)
            .setChronometerFactory(ChronometerFactory.simulated(timeline, durationPerLeap))
}

private fun timeOut(budget: Duration = BUDGET) =
    TimeOutException(context = DummyInstances.executionContext, exceededDuration = budget)

private val natTheory: Theory =
    logicProgramming {
        theoryOf(
            fact { "nat"("z") },
            rule { "nat"("s"(Z)) impliedBy "nat"(Z) },
        )
    }

private val loopTheory: Theory =
    logicProgramming {
        theoryOf(rule { "loop" impliedBy "loop" })
    }

/**
 * Conformance tests for timeout enforcement (`SolveOptions.timeout`), on simulated time unless stated otherwise:
 * that a computation exceeding its budget halts with a [TimeOutException], wherever expiration is detected (state
 * transitions, `sleep/1`, nested resolutions), that `catch/3` can't intercept it, that backtracking doesn't refund
 * time, how the budget applies to lazily consumed solutions, and the exact boundary semantics (strict expiration).
 */
fun timeoutContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // ?- sleep(2000). with a 1 s budget, in real time: a smoke test of the default chronometer
    scenario("timeout/sleep-real-time") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            solver.assertHasPredicateInAPI(Sleep)
            val query = "sleep"(2.seconds.inWholeMilliseconds)
            assertSolutionEquals(listOf(query.halt(timeOut(1.seconds))), solver.solveList(query, 1.seconds))
        }
    }

    // ?- sleep(1000). with a 20 ms simulated budget: halts as soon as the budget is over, without waiting 1 s
    scenario("timeout/sleep") {
        logicProgramming {
            val time = SimulatedTime()
            val query = "sleep"(1000)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, time.options)
            assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
            time.timeline.now shouldBe BUDGET + 1.nanoseconds
        }
    }

    // ?- sleep(15). with a 20 ms simulated budget: succeeds, after exactly 15 ms of virtual time
    scenario("timeout/sleep-within-budget") {
        logicProgramming {
            val time = SimulatedTime()
            val query = "sleep"(15)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, time.options)
            assertSolutionEquals(listOf(query.yes()), solutions)
            time.timeline.now shouldBe 15.milliseconds
        }
    }

    // ?- sleep(20). with a 20 ms simulated budget: expiration is strict, so reaching the deadline exactly is in time
    scenario("timeout/exact-deadline-is-in-time") {
        logicProgramming {
            val query = "sleep"(BUDGET.inWholeMilliseconds)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, SimulatedTime().options)
            assertSolutionEquals(listOf(query.yes()), solutions)
        }
    }

    // ?- sleep(21). with a 20 ms simulated budget: just past the deadline
    scenario("timeout/just-past-the-deadline-is-late") {
        logicProgramming {
            val query = "sleep"(BUDGET.inWholeMilliseconds + 1)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, SimulatedTime().options)
            assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
        }
    }

    // ?- true. with a zero budget: in time if no time elapses at all, late as soon as any does
    scenario("timeout/zero-budget") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = truthOf(true)
            val instant = SimulatedTime(timeout = Duration.ZERO)
            assertSolutionEquals(listOf(query.yes()), solver.solveList(query, instant.options))
            val costly = SimulatedTime(timeout = Duration.ZERO, durationPerLeap = LEAP)
            assertSolutionEquals(listOf(query.halt(timeOut(Duration.ZERO))), solver.solveList(query, costly.options))
        }
    }

    // ?- loop. (loop :- loop.) with a 20 ms budget and 1 ms per step: halts right after the deadline
    scenario("timeout/infinite-recursion") {
        logicProgramming {
            val time = SimulatedTime(durationPerLeap = LEAP)
            val query = atomOf("loop")
            val solutions =
                solverFactory
                    .solverWithDefaultBuiltins(
                        staticKb = loopTheory,
                    ).solveList(query, time.options)
            assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
            time.timeline.now shouldBeGreaterThan BUDGET
            time.timeline.now shouldBeLessThanOrEqualTo BUDGET + LEAP
        }
    }

    // ?- catch(loop, _, true). timeouts are resource exhaustion, not errors: catch/3 can't intercept them
    scenario("timeout/catch-cannot-intercept-a-transition-timeout") {
        logicProgramming {
            val query = "catch"("loop", `_`, true)
            val solutions =
                solverFactory
                    .solverWithDefaultBuiltins(staticKb = loopTheory)
                    .solveList(query, SimulatedTime(durationPerLeap = LEAP).options)
            assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
        }
    }

    // ?- catch(sleep(1000), _, true). same, when the timeout is detected by sleep/1
    scenario("timeout/catch-cannot-intercept-a-sleep-timeout") {
        logicProgramming {
            val query = "catch"("sleep"(1000), `_`, true)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, SimulatedTime().options)
            assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
        }
    }

    // ?- member(X, [1, 2, 3]), sleep(8), X == 3. backtracking doesn't refund the 8 ms each failed attempt took, so
    // the third attempt ends at 24 ms, past the 20 ms budget
    scenario("timeout/backtracking-does-not-refund-time") {
        logicProgramming {
            val query = "member"(X, listOf(1, 2, 3)) and "sleep"(8) and "=="(X, 3)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, SimulatedTime().options)
            assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
        }
    }

    // ?- member(X, [1, 2, 3]), sleep(15). each solution gets the full 20 ms budget, as resuming the search for the
    // next one restarts the chronometer (member/2 leaves a choice point, hence the final `no`)
    scenario("timeout/each-solution-gets-the-full-budget") {
        logicProgramming {
            val query = "member"(X, listOf(1, 2, 3)) and "sleep"(15)
            val solutions = solverFactory.solverWithDefaultBuiltins().solveList(query, SimulatedTime().options)
            assertSolutionEquals((1..3).map { query.yes(X to it) } + query.no(), solutions)
        }
    }

    // ?- member(X, [1, 2]). time the caller spends between consuming two solutions does not count
    scenario("timeout/time-between-solutions-is-not-counted") {
        logicProgramming {
            val time = SimulatedTime()
            val query = "member"(X, listOf(1, 2))
            val solutions = solverFactory.solverWithDefaultBuiltins().solve(query, time.options).iterator()
            assertSolutionEquals(listOf(query.yes(X to 1)), listOf(solutions.next()))
            time.timeline.advanceBy(1.hours)
            assertSolutionEquals(listOf(query.yes(X to 2)), listOf(solutions.next()))
        }
    }

    // ?- findall(N, nat(N), L). an infinite generator under findall/3: the nested resolution is bounded by the outer
    // deadline, so it halts on its first step past it, and the outer resolution on its own next step
    scenario("timeout/infinite-find-all") {
        testInfiniteCollectingGoal { "findall"(N, "nat"(N), L) }
    }

    // Same as timeout/infinite-find-all, with bagof/3
    scenario("timeout/infinite-bag-of") {
        testInfiniteCollectingGoal { "bagof"(N, "nat"(N), L) }
    }

    // Same as timeout/infinite-find-all, with setof/3
    scenario("timeout/infinite-set-of") {
        testInfiniteCollectingGoal { "setof"(N, "nat"(N), L) }
    }
}

private fun ContractScope.testInfiniteCollectingGoal(goalProvider: LogicProgrammingScope.() -> Struct) {
    logicProgramming {
        val solver = solverFactory.solverWithDefaultBuiltins(staticKb = natTheory)
        solver.assertHasPredicateInAPI(FindAll)
        val time = SimulatedTime(durationPerLeap = LEAP)
        val query = goalProvider()
        val solutions = solver.solveList(query, time.options)
        assertSolutionEquals(listOf(query.halt(timeOut())), solutions)
        time.timeline.now shouldBeGreaterThan BUDGET
        time.timeline.now shouldBeLessThanOrEqualTo BUDGET + LEAP * 2
    }
}
