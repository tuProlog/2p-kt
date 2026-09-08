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
import it.unibo.tuprolog.unify.Unificator

/**
 * Base implementation of every `:solve-classic` [it.unibo.tuprolog.solve.Solver]/[it.unibo.tuprolog.solve.MutableSolver]
 * ([ClassicSolver] and [MutableClassicSolver], both `internal`, are the only two concrete subclasses; instances
 * are normally obtained through [ClassicSolverFactory], which is what `Solver.prolog`/`Solver.classic` resolve to).
 *
 * What this class actually contributes over [AbstractSolver] is [solveImpl]: turning a goal into a
 * [Sequence] of [Solution]s by driving the [it.unibo.tuprolog.solve.classic.fsm.State] finite-state machine
 * described by the `:solve-classic` state-machine explanation page. Concretely, it builds the root
 * [ClassicExecutionContext] for the query, wraps a fresh [it.unibo.tuprolog.solve.classic.fsm.StateInit] into a
 * [SolutionIterator] (via the abstract [solutionIterator] hook, which lets subclasses choose a plain or
 * hijackable iterator), and exposes that iterator as a lazy [Sequence] -- so pulling the next [Solution] from the
 * sequence is exactly what drives the state machine one step further, rather than eagerly computing every
 * solution up front. [currentContext] is kept in sync with the state machine's progress via
 * [updateCurrentContextAfterStateTransition], which every [SolutionIterator] created here is wired to call after
 * each transition.
 *
 * Running resolution as an explicit loop over [it.unibo.tuprolog.solve.classic.fsm.State] values, instead of
 * host-language recursion, is what lets a resolution step be paused, resumed, or inspected without unwinding a
 * JVM/JS call stack, and sidesteps host stack-depth limits for deeply recursive Prolog programs (the "call
 * stack" here is the [ClassicExecutionContext] parent chain -- ordinary heap data).
 *
 * @see ClassicSolverFactory
 * @see ClassicExecutionContext
 * @see SolutionIterator
 */
abstract class AbstractClassicSolver(
    unificator: Unificator = Unificator.default,
    libraries: Runtime = Runtime.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(unificator),
    initialDynamicKb: Theory = MutableTheory.empty(unificator),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard(),
    trustKb: Boolean = false,
) : AbstractSolver<ClassicExecutionContext>(
        unificator,
        libraries,
        flags,
        initialStaticKb,
        initialDynamicKb,
        inputChannels,
        outputChannels,
        trustKb,
    ) {
    override lateinit var currentContext: ClassicExecutionContext

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
    ) = ClassicExecutionContext(
        unificator = unificator,
        libraries = libraries,
        flags = flags,
        staticKb = if (trustKb) staticKb.toImmutableTheory() else Theory.empty(unificator),
        dynamicKb = if (trustKb) dynamicKb.toMutableTheory() else MutableTheory.empty(unificator),
        operators = getAllOperators(libraries).toOperatorSet(),
        inputChannels = inputChannels,
        outputChannels = outputChannels,
        startTime = 0,
    )

    /**
     * Builds the root [ClassicExecutionContext] for [goal] (carrying over this solver's current [Theory] knowledge
     * bases, [Runtime] libraries, [FlagStore], channels and [SolveOptions.timeout]) and returns the lazy
     * [Sequence] of [Solution]s obtained by driving a fresh [it.unibo.tuprolog.solve.classic.fsm.StateInit]
     * through the state machine via [solutionIterator].
     */
    final override fun solveImpl(
        goal: Struct,
        options: SolveOptions,
    ): Sequence<Solution> {
        currentContext =
            ClassicExecutionContext(
                unificator = unificator,
                query = goal,
                libraries = libraries,
                flags = flags,
                staticKb = staticKb.toImmutableTheory(),
                dynamicKb = dynamicKb.toMutableTheory(),
                operators = operators,
                inputChannels = inputChannels,
                outputChannels = outputChannels,
                customData = currentContext.customData,
                startTime = currentTimeInstant(),
                maxDuration = options.timeout,
            )
        return solutionIterator(StateInit(currentContext), this::updateCurrentContextAfterStateTransition).asSequence()
    }

    /**
     * Hook through which subclasses choose which [SolutionIterator] flavour drives resolution from [initialState]
     * onwards, invoking [onStateTransition] after every step ([ClassicSolver] always returns a plain
     * [SolutionIterator]; nothing in this module currently returns a [MutableSolutionIterator] here, though the
     * hook is shaped to allow it).
     */
    protected abstract fun solutionIterator(
        initialState: State,
        onStateTransition: (State, State, Long) -> Unit,
    ): SolutionIterator

    @Suppress("UNUSED_PARAMETER")
    private fun updateCurrentContextAfterStateTransition(
        source: State,
        destination: State,
        index: Long,
    ) {
        require(destination.context.step == index)
        currentContext = destination.context
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
    ): AbstractClassicSolver

    abstract override fun clone(): AbstractClassicSolver
}
