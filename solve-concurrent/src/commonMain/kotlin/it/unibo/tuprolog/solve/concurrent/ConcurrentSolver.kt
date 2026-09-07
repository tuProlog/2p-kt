package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.js.JsName

/**
 * A [Solver] whose resolution strategy explores the alternatives of a goal's search tree -- matching clauses at a
 * choice point, the branches of a disjunction, the several solutions of a backtracking primitive -- concurrently,
 * as independent Kotlin coroutines, rather than one at a time as `:solve-classic`/`:solve-streams` do. Obtained via
 * [ConcurrentSolverFactory] or, generically, via [it.unibo.tuprolog.solve.Solver.concurrent].
 *
 * Each coroutine models a single [it.unibo.tuprolog.solve.concurrent.fsm.State] transition (see that package for
 * how a resolution step is represented); whenever a transition has several possible successors, one coroutine is
 * launched per successor, all of them free to run in parallel on whatever dispatcher backs the current platform.
 * As a consequence, __the order in which [Solution]s are produced is not guaranteed to match the left-to-right,
 * depth-first order of standard SLD resolution__ -- unlike `:solve-classic`, whose solutions always come out in
 * that deterministic order. Prefer this solver when a goal has independent, parallelizable alternatives worth
 * spreading across CPU cores (e.g. an N-queens-style search with many disjoint branches) and solution order does
 * not matter; prefer `:solve-classic` (`it.unibo.tuprolog.solve.Solver.prolog`) when ISO-conformant, deterministic
 * solution ordering is required, or when the overhead of spawning a coroutine per choice point would outweigh the
 * parallelism gained (e.g. goals with few, cheap alternatives).
 *
 * __Concurrency caveat__: mutating operations on a [it.unibo.tuprolog.solve.MutableSolver] built on top of this
 * strategy (e.g. `assertZ`/`retract` performed by a primitive while other branches are still running) are not
 * synchronized against one another; concurrently mutating the dynamic knowledge base from multiple branches of the
 * same resolution can race and lose updates. Read-only resolution (the common case) is unaffected, since each
 * branch carries its own immutable [ConcurrentExecutionContext].
 *
 * ```kotlin
 * val solver = ConcurrentSolverFactory.solverWithDefaultBuiltins(staticKb = theory)
 * val channel = solver.solveConcurrently(goal, SolveOptions.allLazily())
 * for (solution in channel) {
 *     println(solution)
 * }
 * ```
 *
 * @see it.unibo.tuprolog.solve.Solver.concurrent
 * @see ConcurrentSolverFactory
 */
interface ConcurrentSolver : Solver {
    /**
     * Solves [goal] according to [options], returning immediately with a [ReceiveChannel] that every concurrently
     * running branch of the search tree publishes its [Solution]s to as soon as it reaches one, rather than the
     * [Sequence] returned by [Solver.solve]. [Solver.solve] on a [ConcurrentSolver] is implemented in terms of this
     * method, bridging the channel back into a blocking [Sequence].
     *
     * As with [Solver.solve], how many solutions are produced is capped by [SolveOptions.limit] -- once reached,
     * every coroutine still exploring other branches is cancelled (see [ConcurrentResolutionHandle]) -- and the
     * whole resolution is capped in time by [SolveOptions.timeout].
     */
    @JsName("solveConcurrently")
    fun solveConcurrently(
        goal: Struct,
        options: SolveOptions,
    ): ReceiveChannel<Solution>

    /** Same as [Solver.copy], but statically typed to return a [ConcurrentSolver]. */
    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): ConcurrentSolver

    /** Same as [Solver.clone], but statically typed to return a [ConcurrentSolver]. */
    override fun clone(): ConcurrentSolver
}
