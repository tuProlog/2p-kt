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

/**
 * General type for logic solvers, i.e. any entity capable of solving some logic query -- provided as a [Struct] --
 * according to some logic, implementing one or more inference rule, via some resolution strategy.
 *
 * A [Solver] is deliberately strategy-agnostic: this module defines no resolution algorithm at all, only the
 * contract that every implementation (`:solve-classic`'s SLD-NF resolution, `:solve-streams`'s side-effect-free
 * strategy, `:solve-concurrent`, `:solve-problog`) must honour. Everything a resolution strategy needs to read
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
 * val solutions = solver.solve(Struct.of("append", listA, listB, result))
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
        timeout: TimeDuration,
    ): Sequence<Solution> = solve(goal, SolveOptions.allLazilyWithTimeout(timeout))

    /** Shorthand for [solve] with [SolveOptions.DEFAULT] (i.e. all solutions, lazily, without a timeout). */
    @JsName("solve")
    fun solve(goal: Struct): Sequence<Solution> = solve(goal, SolveOptions.DEFAULT)

    /**
     * Solves [goal], returning a (possibly infinite) [Sequence] of [Solution]s, computed according to `options`.
     *
     * Whether solutions are computed as the sequence is consumed, or eagerly ahead of time, depends on
     * [SolveOptions.isLazy]; how many solutions are produced is capped by [SolveOptions.limit], and the overall
     * resolution process is capped in time by [SolveOptions.timeout].
     */
    @JsName("solveWithOptions")
    fun solve(
        goal: Struct,
        options: SolveOptions,
    ): Sequence<Solution>

    /** Shorthand for [solveList] with `options` set to [SolveOptions.allLazilyWithTimeout] of [timeout]. */
    @JsName("solveListWithTimeout")
    fun solveList(
        goal: Struct,
        timeout: TimeDuration,
    ): List<Solution> = solve(goal, timeout).toList()

    /** Shorthand for [solveList] with [SolveOptions.DEFAULT]. */
    @JsName("solveList")
    fun solveList(goal: Struct): List<Solution> = solve(goal).toList()

    /**
     * Eagerly solves [goal] and collects every produced [Solution] into a [List]. Unlike [solve], this always
     * computes solutions eagerly regardless of [SolveOptions.isLazy] -- be mindful of goals with infinite (or very
     * large) solution sets, which will make this method never return (or exhaust memory).
     */
    @JsName("solveListWithOptions")
    fun solveList(
        goal: Struct,
        options: SolveOptions,
    ): List<Solution> = solve(goal, options).toList()

    /** Shorthand for [solveOnce] with a timeout, and a limit of `1` solution. */
    @JsName("solveOnceWithTimeout")
    fun solveOnce(
        goal: Struct,
        timeout: TimeDuration,
    ): Solution = solve(goal, SolveOptions.someLazilyWithTimeout(1, timeout)).first()

    /** Shorthand for [solveOnce] with [SolveOptions.someLazily] of `1`. */
    @JsName("solveOnce")
    fun solveOnce(goal: Struct): Solution = solve(goal, SolveOptions.someLazily(1)).first()

    /**
     * Solves [goal] and eagerly returns its first [Solution] only, regardless of `options`' [SolveOptions.limit]
     * (which is overridden to `1` via [SolveOptions.setLimit]).
     */
    @JsName("solveOnceWithOptions")
    fun solveOnce(
        goal: Struct,
        options: SolveOptions,
    ): Solution = solve(goal, options.setLimit(1)).first()

    /**
     * Creates a new [Solver], sharing the same resolution strategy as this one, but with every explicitly-provided
     * argument replacing the corresponding piece of state; arguments left unspecified default to this solver's
     * current value. Useful to derive variants of a solver overriding just a few "mutable aspects"
     * (e.g. redirecting [stdOut] while keeping everything else identical).
     */
    @JsName("copy")
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
        /**
         * The [SolverFactory] for the classic, ISO-standard SLD-NF resolution solver (`:solve-classic`).
         *
         * @see prolog
         */
        @JvmStatic
        @get:JvmName("classic")
        @JsName("classic")
        @Deprecated(
            message = "This method is being renamed into \"prolog\" and its usage in this form is now deprecated",
            replaceWith = ReplaceWith("Solver.prolog"),
        )
        val classic: SolverFactory by lazy { classicSolverFactory() }

        /** The [SolverFactory] for the classic, ISO-standard SLD-NF resolution solver (`:solve-classic`). */
        @JvmStatic
        @get:JvmName("prolog")
        @JsName("prolog")
        val prolog: SolverFactory by lazy { classicSolverFactory() }

        /** The [SolverFactory] for the probabilistic-logic-programming solver (`:solve-problog`). */
        @JvmStatic
        @get:JvmName("problog")
        @JsName("problog")
        val problog: SolverFactory by lazy { problogSolverFactory() }

        /**
         * The [SolverFactory] for the experimental, more side-effect-free solver (`:solve-streams`).
         *
         * Marked deprecated because this implementation is experimental and not mature enough for general-purpose
         * usage yet.
         */
        @JvmStatic
        @get:JvmName("streams")
        @JsName("streams")
        @Deprecated("The \"Streams\" solver is experimental and not mature enough for general purpose usage")
        val streams: SolverFactory by lazy { streamsSolverFactory() }

        /** The [SolverFactory] for the solver that parallelizes resolution (`:solve-concurrent`). */
        @JvmStatic
        @get:JvmName("concurrent")
        @JsName("concurrent")
        val concurrent: SolverFactory by lazy { concurrentSolverFactory() }
    }
}
