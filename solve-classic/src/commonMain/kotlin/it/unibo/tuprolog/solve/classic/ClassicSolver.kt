package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.classic.fsm.StateInit
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.impl.AbstractSolver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

internal open class ClassicSolver(
    libraries: Libraries = Libraries.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(),
    initialDynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore = InputStore.default(),
    outputChannels: OutputStore = OutputStore.default(),
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
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<PrologWarning> = OutputChannel.warn(),
        trustKb: Boolean = false
    ) : this(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        InputStore.default(stdIn),
        OutputStore.default(stdOut, stdErr, warnings),
        trustKb
    )

    final override fun initializeContext(
        libraries: Libraries,
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

    override fun solveImpl(goal: Struct, options: SolveOptions): Sequence<Solution> {
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
        return SolutionIterator.of(StateInit(currentContext)) { _, newState, newStep ->
            require(newState.context.step == newStep)
            currentContext = newState.context
        }.asSequence()
    }

    override fun copy(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) = ClassicSolver(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override fun clone(): ClassicSolver = copy()
}
