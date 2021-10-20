package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.js.JsName

interface ConcurrentSolver : Solver {

    val resolutionScope: CoroutineScope

    @JsName("solveConcurrently")
    fun solveConcurrently(goal: Struct, options: SolveOptions): ReceiveChannel<Solution>

    override fun solveOnce(goal: Struct, timeout: TimeDuration): Solution =
        solve(goal, SolveOptions.someLazilyWithTimeout(1, timeout))
            .firstOrNull { !it.isNo } ?: Solution.no(goal)

    override fun solveOnce(goal: Struct): Solution =
        solve(goal, SolveOptions.someLazily(1))
            .firstOrNull { !it.isNo } ?: Solution.no(goal)

    override fun solveOnce(goal: Struct, options: SolveOptions): Solution =
        solve(goal, options.setLimit(1))
            .firstOrNull { !it.isNo } ?: Solution.no(goal)

    override fun copy(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): ConcurrentSolver

    override fun clone(): ConcurrentSolver
}
