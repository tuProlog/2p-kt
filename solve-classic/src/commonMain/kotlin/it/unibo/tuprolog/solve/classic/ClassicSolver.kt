package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

internal open class ClassicSolver : AbstractClassicSolver {

    constructor(
        unificator: Unificator = Unificator.default,
        libraries: Runtime = Runtime.empty(),
        flags: FlagStore = FlagStore.empty(),
        initialStaticKb: Theory = Theory.empty(unificator),
        initialDynamicKb: Theory = MutableTheory.empty(unificator),
        inputChannels: InputStore = InputStore.fromStandard(),
        outputChannels: OutputStore = OutputStore.fromStandard(),
        trustKb: Boolean = false
    ) : super(unificator, libraries, flags, initialStaticKb, initialDynamicKb, inputChannels, outputChannels, trustKb)

    constructor(
        unificator: Unificator = Unificator.default,
        libraries: Runtime = Runtime.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(unificator),
        dynamicKb: Theory = MutableTheory.empty(unificator),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<Warning> = OutputChannel.warn(),
        trustKb: Boolean = false
    ) : super(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings, trustKb)

    override fun solutionIterator(
        initialState: State,
        onStateTransition: (State, State, Long) -> Unit
    ) = SolutionIterator.of(initialState, onStateTransition)

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
    ) = ClassicSolver(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun clone(): ClassicSolver = copy()
}
