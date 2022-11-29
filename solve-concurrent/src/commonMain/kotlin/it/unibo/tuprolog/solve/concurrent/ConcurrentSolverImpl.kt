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
import it.unibo.tuprolog.solve.concurrent.fsm.toGoals
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.impl.AbstractSolver
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.jvm.Synchronized
import kotlinx.coroutines.channels.Channel as KtChannel

internal open class ConcurrentSolverImpl(
    unificator: Unificator = Unificator.default,
    libraries: Runtime = Runtime.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(unificator),
    initialDynamicKb: Theory = MutableTheory.empty(unificator),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard(),
    trustKb: Boolean = false
) : ConcurrentSolver, AbstractSolver<ConcurrentExecutionContext>(
    unificator,
    libraries,
    flags,
    initialStaticKb,
    initialDynamicKb,
    inputChannels,
    outputChannels,
    trustKb
) {

    constructor(
        unificator: Unificator,
        libraries: Runtime = Runtime.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(unificator),
        dynamicKb: Theory = MutableTheory.empty(unificator),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<Warning> = OutputChannel.warn(),
        trustKb: Boolean = false
    ) : this(
        unificator,
        libraries,
        flags,
        staticKb,
        dynamicKb,
        InputStore.fromStandard(stdIn),
        OutputStore.fromStandard(stdOut, stdErr, warnings),
        trustKb
    )

    @get:Synchronized
    @set:Synchronized
    override lateinit var currentContext: ConcurrentExecutionContext

    private fun CoroutineScope.handleAsyncStateTransition(state: State, handle: ConcurrentResolutionHandle): Job =
        launch {
            if (state is EndState) {
                handle.publishSolutionAndTerminateResolutionIfNeed(state.solution, this)
            } else {
                for (it in state.next()) {
                    handleAsyncStateTransition(it, handle)
                    yield()
                }
            }
        }

    private fun Sequence<Solution>.ensureAtMostOneNegative(): Sequence<Solution> = sequence {
        var lastNegative: Solution.No? = null
        val i = iterator()
        while (i.hasNext()) {
            when (val it = i.next()) {
                is Solution.No -> {
                    lastNegative = it
                }
                else -> {
                    yield(it)
                }
            }
        }
        lastNegative?.let { yield(it) }
    }

    private suspend fun startAsyncResolution(initialState: State, handle: ConcurrentResolutionHandle) = coroutineScope {
        handleAsyncStateTransition(initialState, handle).join()
        handle.closeSolutionChannelWithNoSolutionIfNeeded(initialState.context.query)
    }

    private fun initialState(goal: Struct, options: SolveOptions): State {
        currentContext = ConcurrentExecutionContext(
            goals = goal.toGoals(),
            step = 1,
            query = goal,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb.toImmutableTheory(),
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            customData = currentContext.customData,
            maxDuration = options.timeout,
            startTime = currentTimeInstant()
        )
        return StateGoalSelection(currentContext)
    }

    override fun solveConcurrently(goal: Struct, options: SolveOptions): ReceiveChannel<Solution> {
        val channel = KtChannel<Solution>(KtChannel.UNLIMITED)
        val initialState = initialState(goal, options)
        val handle = ConcurrentResolutionHandle(options, channel)
        val resolutionScope = createScope()
        resolutionScope.launch {
            startAsyncResolution(initialState, handle)
        }
        return channel
    }

    override fun solveImpl(goal: Struct, options: SolveOptions): Sequence<Solution> {
        return solveConcurrently(goal, options).toSequence().ensureAtMostOneNegative()
    }

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ) = ConcurrentSolverImpl(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun clone(): ConcurrentSolverImpl = copy()

    override fun initializeContext(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        trustKb: Boolean
    ): ConcurrentExecutionContext = ConcurrentExecutionContext(
        unificator = unificator,
        libraries = libraries,
        flags = flags,
        staticKb = if (trustKb) staticKb.toImmutableTheory() else Theory.empty(unificator),
        dynamicKb = if (trustKb) dynamicKb.toMutableTheory() else MutableTheory.empty(unificator),
        operators = getAllOperators(libraries).toOperatorSet(),
        inputChannels = inputChannels,
        outputChannels = outputChannels,
        startTime = 0L
    )
}
