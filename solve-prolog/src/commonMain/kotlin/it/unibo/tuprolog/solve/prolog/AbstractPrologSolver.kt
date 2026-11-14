package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolutionStream
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.impl.AbstractSolver
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.machine.Cancellation
import it.unibo.tuprolog.solve.prolog.machine.PrologMachine
import it.unibo.tuprolog.solve.prolog.machine.PrologSolutionStream
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The base of `:solve-prolog`'s solvers: it keeps the solver's [PrologExecutionContext] (its world state between
 * queries), and answers each query with a fresh stateful machine ([PrologMachine], #1010), which reports every change
 * of the world back to [currentContext].
 */
internal abstract class AbstractPrologSolver(
    unificator: Unificator = Unificator.default,
    libraries: Runtime = Runtime.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(unificator),
    initialDynamicKb: Theory = MutableTheory.empty(unificator),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard(),
    trustKb: Boolean = false,
) : AbstractSolver<PrologExecutionContext>(
        unificator,
        libraries,
        flags,
        initialStaticKb,
        initialDynamicKb,
        inputChannels,
        outputChannels,
        trustKb,
    ) {
    override lateinit var currentContext: PrologExecutionContext

    // the cancellation of the stream this solver was created in (see PrologExecutionContext.createSolver), if any
    internal var inheritedCancellation: Cancellation? = null

    // the stream being opened by openStream, for solveImpl to hand to its machine
    private var starting: PrologSolutionStream? = null

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
        trustKb: Boolean = false,
    ) : this(
        unificator,
        libraries,
        flags,
        staticKb,
        dynamicKb,
        InputStore.fromStandard(stdIn),
        OutputStore.fromStandard(stdOut, stdErr, warnings),
        trustKb,
    )

    final override fun initializeContext(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        trustKb: Boolean,
    ) = PrologExecutionContext(
        unificator = unificator,
        libraries = libraries,
        flags = flags,
        staticKb = if (trustKb) staticKb.toImmutableTheory() else Theory.empty(unificator),
        dynamicKb = if (trustKb) dynamicKb.toMutableTheory() else MutableTheory.empty(unificator),
        operators = getAllOperators(libraries).toOperatorSet(),
        inputChannels = inputChannels,
        outputChannels = outputChannels,
    )

    /**
     * Builds the [PrologExecutionContext] of a resolution of [goal], from this solver's knowledge bases, libraries,
     * flags and channels and from [options], and answers it with a fresh [PrologMachine].
     */
    final override fun solveImpl(
        goal: Goal,
        options: SolveOptions,
    ): Sequence<Solution> {
        currentContext =
            PrologExecutionContext(
                unificator = unificator,
                libraries = libraries,
                flags = flags,
                staticKb = staticKb.toImmutableTheory(),
                dynamicKb = dynamicKb.toMutableTheory(),
                operators = operators,
                inputChannels = inputChannels,
                outputChannels = outputChannels,
                customData = currentContext.customData,
                chronometer = options.createChronometer(),
                counters = options.counters,
                interception = options.interceptor?.let(::Interception),
                ruleSelection = options.ruleSelection,
                goalSelection = options.goalSelection,
                cancellation = starting?.cancellation ?: inheritedCancellation,
            )
        val machine = PrologMachine(currentContext, goal, options.branchScheduler) { currentContext = it }
        starting?.machine = machine
        return machine.asSequence()
    }

    // the machine checks the cancellation at every step, and nested resolutions share it
    final override fun openStream(
        goal: Goal,
        solutions: () -> Sequence<Solution>,
    ): SolutionStream {
        val stream = PrologSolutionStream(Cancellation(inheritedCancellation))
        starting = stream
        try {
            stream.solutions = solutions().iterator()
        } finally {
            starting = null
        }
        return stream
    }

    abstract override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>,
    ): AbstractPrologSolver

    abstract override fun clone(): AbstractPrologSolver
}
