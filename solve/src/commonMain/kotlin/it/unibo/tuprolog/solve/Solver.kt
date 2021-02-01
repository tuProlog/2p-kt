package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Represents a Prolog Goal solver
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

    companion object {

        @JvmStatic
        @JsName("classic")
        val classic: SolverFactory by lazy {
            classicSolverFactory()
        }

        @JvmStatic
        @JsName("streams")
        val streams: SolverFactory by lazy {
            streamsSolverFactory()
        }
    }
}
