package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.fsm.StateInit
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.impl.AbstractSolver
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

abstract class AbstractClassicSolver(
    libraries: Runtime = Runtime.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(),
    initialDynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard(),
    trustKb: Boolean = false
) : AbstractSolver<ClassicExecutionContext>(
    libraries,
    flags,
    initialStaticKb,
    initialDynamicKb,
    inputChannels,
    outputChannels,
    trustKb
) {

    override lateinit var currentContext: ClassicExecutionContext

    constructor(
        libraries: Runtime = Runtime.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<Warning> = OutputChannel.warn(),
        trustKb: Boolean = false
    ) : this(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        InputStore.fromStandard(stdIn),
        OutputStore.fromStandard(stdOut, stdErr, warnings),
        trustKb
    )

    final override fun initializeContext(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        trustKb: Boolean
    ) = ClassicExecutionContext(
        libraries = libraries,
        flags = flags,
        staticKb = if (trustKb) staticKb.toImmutableTheory() else Theory.empty(),
        dynamicKb = if (trustKb) dynamicKb.toMutableTheory() else MutableTheory.empty(),
        operators = getAllOperators(libraries).toOperatorSet(),
        inputChannels = inputChannels,
        outputChannels = outputChannels
    )

    final override fun solveImpl(goal: Struct, options: SolveOptions): Sequence<Solution> {
        currentContext = ClassicExecutionContext(
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
        return solutionIterator(StateInit(currentContext), this::updateCurrentContextAfterStateTransition).asSequence()
    }

    protected abstract fun solutionIterator(
        initialState: State,
        onStateTransition: (State, State, Long) -> Unit
    ): SolutionIterator

    @Suppress("UNUSED_PARAMETER")
    private fun updateCurrentContextAfterStateTransition(source: State, destination: State, index: Long) {
        require(destination.context.step == index)
        currentContext = destination.context
    }

    abstract override fun copy(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): AbstractClassicSolver

    abstract override fun clone(): AbstractClassicSolver
}
