package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import kotlinx.coroutines.flow.Flow
import kotlin.js.JsName

interface ConcurrentSolver {
    @JsName("solveWithOptions")
    suspend fun solve(goal: Struct, options: SolveOptions): Flow<Solution>
}
