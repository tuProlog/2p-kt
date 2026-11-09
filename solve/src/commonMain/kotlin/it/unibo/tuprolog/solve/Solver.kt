package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.time.Duration

/**
 * General type for logic solvers, i.e. any entity capable of solving some logic query -- provided as a [Struct] --
 * according to some logic, implementing one or more inference rule, via some resolution strategy.
 *
 * A [Solver] is deliberately strategy-agnostic: this module defines no resolution algorithm at all, only the
 * contract that every implementation (`:solve-prolog`'s SLD-NF resolution, `:solve-concurrent`, `:solve-problog`)
 * must honour. Everything a resolution strategy needs to read
 * while solving a goal -- loaded `it.unibo.tuprolog.solve.library.Library`/`Runtime`, [FlagStore], the two
 * [Theory] knowledge bases, I/O [it.unibo.tuprolog.solve.channel.Channel]s -- is exposed through
 * [ExecutionContextAware], which this interface extends.
 *
 * __Solvers are not immutable entities__. Their state may mutate as an effect of solving queries -- e.g. `assert`ing
 * a clause during resolution replaces the dynamic knowledge base with a new [Theory] instance. Between resolutions,
 * a solver's assets can only be swapped by deriving a new [Solver] via [copy], unless the concrete instance also
 * implements [MutableSolver].
 *
 * Instances are usually obtained from a [SolverFactory], several of which are reachable from the companion object,
 * e.g.:
 * ```kotlin
 * val solver = Solver.prolog.solverWithDefaultBuiltins()
 * solver.solve(Struct.of("append", listA, listB, result)).use { solutions ->
 *     for (solution in solutions) println(solution)
 * }
 * ```
 *
 * @see MutableSolver
 * @see Solution
 * @see SolveOptions
 */
interface Solver : ExecutionContextAware {
    /** Shorthand for [solve] with `options` set to [SolveOptions.allLazilyWithTimeout] of [timeout]. */
    @JsName("solveWithTimeout")
    fun solve(
        goal: Struct,
        timeout: Duration,
    ): SolutionStream = solve(goal, SolveOptions.allLazilyWithTimeout(timeout))

    /** Shorthand for [solve] with [SolveOptions.DEFAULT] (i.e. all solutions, lazily, without a timeout). */
    @JsName("solve")
    fun solve(goal: Struct): SolutionStream = solve(goal, SolveOptions.DEFAULT)

    /**
     * Solves [goal], returning its (possibly infinite) solutions, computed according to `options`, as a
     * [SolutionStream]: a single-pass [Sequence] that can be cancelled, and that should be closed once no more
     * solutions are needed, e.g. through `use { }`, to release what the resolution holds (#1010).
     *
     * Whether solutions are computed as the stream is consumed, or eagerly ahead of time, depends on
     * [SolveOptions.isLazy]; how many solutions are produced is capped by [SolveOptions.limit], and the overall
     * resolution process is capped in time by [SolveOptions.timeout]. To collect every solution, call `toList()`
     * on the stream: a resolution that ran to its end holds nothing more.
     */
    @JsName("solveWithOptions")
    fun solve(
        goal: Struct,
        options: SolveOptions,
    ): SolutionStream

    /** Every solution of [goal] within [timeout], as a [List]. */
    @Deprecated(
        "Call solve(goal, timeout).toList() instead, which also closes the SolutionStream",
        ReplaceWith("solve(goal, timeout).toList()"),
    )
    @JsName("solveListWithTimeout")
    fun solveList(
        goal: Struct,
        timeout: Duration,
    ): List<Solution> = solve(goal, timeout).toList()

    /** Every solution of [goal], as a [List]. */
    @Deprecated(
        "Call solve(goal).toList() instead, which also closes the SolutionStream",
        ReplaceWith("solve(goal).toList()"),
    )
    @JsName("solveList")
    fun solveList(goal: Struct): List<Solution> = solve(goal).toList()

    /** Every solution of [goal] computed according to [options], as a [List]. */
    @Deprecated(
        "Call solve(goal, options).toList() instead, which also closes the SolutionStream",
        ReplaceWith("solve(goal, options).toList()"),
    )
    @JsName("solveListWithOptions")
    fun solveList(
        goal: Struct,
        options: SolveOptions,
    ): List<Solution> = solve(goal, options).toList()

    /** Shorthand for [solveOnce] with a timeout, and a limit of `1` solution. */
    @JsName("solveOnceWithTimeout")
    fun solveOnce(
        goal: Struct,
        timeout: Duration,
    ): Solution = solveOnce(goal, SolveOptions.someLazilyWithTimeout(1, timeout))

    /** Shorthand for [solveOnce] with [SolveOptions.someLazily] of `1`. */
    @JsName("solveOnce")
    fun solveOnce(goal: Struct): Solution = solveOnce(goal, SolveOptions.someLazily(1))

    /**
     * Solves [goal] and eagerly returns its first [Solution] only, regardless of `options`' [SolveOptions.limit]
     * (which is overridden to `1` via [SolveOptions.setLimit]); the [SolutionStream] is closed afterwards.
     */
    @JsName("solveOnceWithOptions")
    fun solveOnce(
        goal: Struct,
        options: SolveOptions,
    ): Solution = solve(goal, options.setLimit(1)).use { it.first() }

    /**
     * Creates a new [Solver], sharing the same resolution strategy as this one, but with every explicitly-provided
     * argument replacing the corresponding piece of state; arguments left unspecified default to this solver's
     * current value. Useful to derive variants of a solver overriding just a few "mutable aspects"
     * (e.g. redirecting [stdOut] while keeping everything else identical).
     */
    fun copy(
        unificator: Unificator = this.unificator,
        libraries: Runtime = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        stdIn: InputChannel<String> = this.standardInput,
        stdOut: OutputChannel<String> = this.standardOutput,
        stdErr: OutputChannel<String> = this.standardError,
        warnings: OutputChannel<Warning> = this.warnings,
    ): Solver

    /** Shorthand for [copy] without overriding anything, i.e. an identical (but distinct) [Solver] instance. */
    @JsName("clone")
    fun clone(): Solver = copy()

    companion object {
        /** The [SolverFactory] for the Prolog, ISO-standard SLD-NF resolution solver (`:solve-prolog`). */
        @JvmStatic
        @get:JvmName("prolog")
        @JsName("prolog")
        val prolog: SolverFactory by lazy { prologSolverFactory() }

        /** The [SolverFactory] for the probabilistic-logic-programming solver (`:solve-problog`). */
        @JvmStatic
        @get:JvmName("problog")
        @JsName("problog")
        val problog: SolverFactory by lazy { problogSolverFactory() }

        /** The [SolverFactory] for the solver that parallelizes resolution (`:solve-concurrent`). */
        @JvmStatic
        @get:JvmName("concurrent")
        @JsName("concurrent")
        val concurrent: SolverFactory by lazy { concurrentSolverFactory() }
    }
}
