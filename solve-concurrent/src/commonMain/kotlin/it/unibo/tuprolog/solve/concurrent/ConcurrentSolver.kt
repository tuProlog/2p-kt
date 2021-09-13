package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import kotlinx.coroutines.flow.Flow
import kotlin.js.JsName

interface ConcurrentSolver<E : ExecutionContext> : ExecutionContextAware {

    var currentContext: E

    @JsName("solveConcurrently")
    suspend fun solveConcurrently(goal: Struct): Flow<Solution>

    /*
    @JsName("solveConcurrentlyWithOptions")
    suspend fun solveConcurrently(goal: Struct, options: SolveOptions): Flow<Solution>
    */

    // todo solve overload

    @JsName("copy")
    fun copy(
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        operators: OperatorSet = OperatorSet.EMPTY,
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<Warning> = OutputChannel.warn()
    ): ConcurrentSolver<E>

    @JsName("clone")
    fun clone(): ConcurrentSolver<E> = copy()

    /* todo add factory
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
    */
}
