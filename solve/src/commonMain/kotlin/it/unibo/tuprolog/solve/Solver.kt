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
 * __Solvers are not immutable entities__. Their state may mutate as an effect of solving queries.
 */
interface Solver : ExecutionContextAware {

    @JsName("solveWithTimeout")
    fun solve(goal: Struct, timeout: TimeDuration): Sequence<Solution> =
        solve(goal, SolveOptions.allLazilyWithTimeout(timeout))

    @JsName("solve")
    fun solve(goal: Struct): Sequence<Solution> = solve(goal, SolveOptions.DEFAULT)

    @JsName("solveWithOptions")
    fun solve(goal: Struct, options: SolveOptions): Sequence<Solution>

    @JsName("solveListWithTimeout")
    fun solveList(goal: Struct, timeout: TimeDuration): List<Solution> = solve(goal, timeout).toList()

    @JsName("solveList")
    fun solveList(goal: Struct): List<Solution> = solve(goal).toList()

    @JsName("solveListWithOptions")
    fun solveList(goal: Struct, options: SolveOptions): List<Solution> = solve(goal, options).toList()

    @JsName("solveOnceWithTimeout")
    fun solveOnce(goal: Struct, timeout: TimeDuration): Solution =
        solve(goal, SolveOptions.someLazilyWithTimeout(1, timeout)).first()

    @JsName("solveOnce")
    fun solveOnce(goal: Struct): Solution = solve(goal, SolveOptions.someLazily(1)).first()

    @JsName("solveOnceWithOptions")
    fun solveOnce(goal: Struct, options: SolveOptions): Solution = solve(goal, options.setLimit(1)).first()

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

    @JsName("clone")
    fun clone(): Solver = copy()

    companion object {
        @JvmStatic
        @get:JvmName("classic")
        @JsName("classic")
        @Deprecated(
            message = "This method is being renamed into \"prolog\" and its usage in this form is now deprecated",
            replaceWith = ReplaceWith("Solver.prolog")
        )
        val classic: SolverFactory by lazy { classicSolverFactory() }

        @JvmStatic
        @get:JvmName("prolog")
        @JsName("prolog")
        val prolog: SolverFactory by lazy { classicSolverFactory() }

        @JvmStatic
        @get:JvmName("problog")
        @JsName("problog")
        val problog: SolverFactory by lazy { problogSolverFactory() }

        @JvmStatic
        @get:JvmName("streams")
        @JsName("streams")
        @Deprecated("The \"Streams\" solver is experimental and not mature enough for general purpose usage")
        val streams: SolverFactory by lazy { streamsSolverFactory() }

        @JvmStatic
        @get:JvmName("concurrent")
        @JsName("concurrent")
        val concurrent: SolverFactory by lazy { concurrentSolverFactory() }
    }
}
