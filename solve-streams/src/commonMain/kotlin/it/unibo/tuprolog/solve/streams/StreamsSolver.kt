package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.FinalState
import it.unibo.tuprolog.solve.streams.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateInit
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.buffered

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree in a purely functional way
 *
 * @author Enrico
 */
internal class StreamsSolver constructor(
    unificator: Unificator = Unificator.default,
    libraries: Runtime = Runtime.empty(),
    flags: FlagStore = FlagStore.empty(),
    staticKb: Theory = Theory.empty(unificator),
    dynamicKb: Theory = Theory.empty(unificator),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard()
) : Solver {

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
    ) : this(
        unificator,
        libraries,
        flags,
        staticKb,
        dynamicKb,
        InputStore.fromStandard(stdIn),
        OutputStore.fromStandard(stdOut, stdErr, warnings),
    )

    private var executionContext: StreamsExecutionContext = StreamsExecutionContext(
        unificator,
        libraries,
        flags,
        staticKb,
        dynamicKb,
        getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
        inputChannels,
        outputChannels
    )

    override fun solve(goal: Struct, options: SolveOptions): Sequence<Solution> {
        executionContext = StreamsExecutionContext(
            unificator = unificator,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            maxDuration = options.timeout
        )

        var solutionSequence = solveToFinalStates(
            Solve.Request<StreamsExecutionContext>(
                goal.extractSignature(),
                goal.args,
                executionContext,
            )
        ).map {
            executionContext = it.context.apply(it.solve.sideEffects)
            it.solve.solution.cleanUp()
        }
        if (options.limit > 0) {
            solutionSequence = solutionSequence.take(options.limit)
        }
        if (options.isEager) {
            solutionSequence = solutionSequence.buffered()
        }
        return solutionSequence
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
    ) = StreamsSolver(unificator, libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override val unificator: Unificator
        get() = executionContext.unificator

    override val libraries: Runtime
        get() = executionContext.libraries

    override val flags: FlagStore
        get() = executionContext.flags

    override val staticKb: Theory
        get() = executionContext.staticKb

    override val dynamicKb: Theory
        get() = executionContext.dynamicKb

    override val inputChannels: InputStore
        get() = executionContext.inputChannels

    override val outputChannels: OutputStore
        get() = executionContext.outputChannels

    override val operators: OperatorSet
        get() = executionContext.operators

    internal companion object {

        /** Internal version of other [solve] method, that accepts raw requests and returns raw statemachine final states */
        internal fun solveToFinalStates(goalRequest: Solve.Request<StreamsExecutionContext>): Sequence<FinalState> =
            StateMachineExecutor.execute(StateInit(goalRequest))
                .filterIsInstance<FinalState>()
                .filter { it.solve.solution.query == goalRequest.query }

        /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
        internal fun solveToResponses(goalRequest: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
            solveToFinalStates(goalRequest).map { it.solve }
    }
}
