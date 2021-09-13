package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.concurrent.fsm.EndState
import it.unibo.tuprolog.solve.concurrent.fsm.State
import it.unibo.tuprolog.solve.concurrent.fsm.StateGoalSelection
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.impl.AbstractSolver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlin.jvm.Synchronized

internal open class ConcurrentSolverImpl(
    override val libraries: Libraries = Libraries.empty(),
    override val flags: FlagStore = FlagStore.empty(),
    override val staticKb: Theory = Theory.empty(),
    override val dynamicKb: Theory = MutableTheory.empty(),
    override val operators: OperatorSet,
    override val inputChannels: InputStore = InputStore.fromStandard(),
    override val outputChannels: OutputStore = OutputStore.fromStandard()
): ConcurrentSolver<ConcurrentExecutionContext> {

    @get:Synchronized
    @set:Synchronized
    override lateinit var currentContext: ConcurrentExecutionContext

    private fun collector(state: State, channel: SendChannel<State>, scope: CoroutineScope) {
        scope.launch {
            channel.send(state)
            state.next().forEach { collector(it,channel, scope) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun solveImpl(goal: Struct, options: SolveOptions): Flow<Solution> =
        channelFlow<State> { collector(initialState(), channel, this) }
            .filterIsInstance<EndState>()
            .map { currentContext = it.context; it.solution }
            .flowOn(Dispatchers.Default)

    private fun initialState(): State = StateGoalSelection(currentContext)

    override suspend fun solveConcurrently(goal: Struct): Flow<Solution> = solveImpl(goal, SolveOptions.DEFAULT)

    override fun copy(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ) = ConcurrentSolverImpl(libraries, flags, staticKb, dynamicKb, operators, InputStore.fromStandard(stdIn), OutputStore.fromStandard(stdOut, stdErr, warnings))

    override fun clone(): ConcurrentSolverImpl = copy()

    /*
    @JsName("solveWithOptions")
    fun solve(goal: Struct, options: SolveOptions): Flow<Solution>
    */
}
