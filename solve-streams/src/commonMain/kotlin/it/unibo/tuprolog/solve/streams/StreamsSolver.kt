package it.unibo.tuprolog.solve.streams

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.FinalState
import it.unibo.tuprolog.solve.streams.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateInit
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.buffered

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree in a purely functional way
 *
 * @author Enrico
 */
internal class StreamsSolver constructor(
    libraries: Libraries = Libraries.empty(),
    flags: FlagStore = FlagStore.empty(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = Theory.empty(),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard()
) : Solver {

    constructor(
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<PrologWarning> = OutputChannel.warn(),
    ) : this(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        InputStore.fromStandard(stdIn),
        OutputStore.fromStandard(stdOut, stdErr, warnings),
    )

    private var executionContext: ExecutionContext = StreamsExecutionContext(
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
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            inputChannels = inputChannels,
            outputChannels = outputChannels
        )

        var solutionSequence = solveToFinalStates(
            Solve.Request(
                goal.extractSignature(),
                goal.argsList,
                executionContext as StreamsExecutionContext,
                executionMaxDuration = options.timeout
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
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) = StreamsSolver(libraries, flags, staticKb, dynamicKb, stdIn, stdOut, stdErr, warnings)

    override val libraries: Libraries
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
