package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory

/**
 * The execution context implementation for [StreamsSolver]
 *
 * @author Enrico
 */
internal data class StreamsExecutionContext(
    override val libraries: Libraries = Libraries(),
    override val flags: FlagStore = FlagStore.EMPTY,
    override val staticKb: Theory = Theory.empty(),
    override val dynamicKb: Theory = Theory.empty(),
    override val operators: OperatorSet = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
    override val inputChannels: Map<String, InputChannel<*>> = ExecutionContextAware.defaultInputChannels(),
    override val outputChannels: Map<String, OutputChannel<*>> = ExecutionContextAware.defaultOutputChannels(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,
    /** The side effects manager to be used during resolution process */
    val sideEffectManager: SideEffectManagerImpl = SideEffectManagerImpl()
) : ExecutionContext {

    constructor(context: ExecutionContext, newCurrentSubstitution: Substitution.Unifier) : this( // to be tested
        context.libraries,
        context.flags,
        context.staticKb,
        context.dynamicKb,
        context.operators,
        context.inputChannels,
        context.outputChannels,
        newCurrentSubstitution,
        (context as? StreamsExecutionContext)?.solverStrategies ?: SolverStrategies.prologStandard,
        (context as? StreamsExecutionContext)?.sideEffectManager ?: SideEffectManagerImpl()
    )

    override val procedure: Struct?
        get() = sideEffectManager.logicalParentRequests.map { it.query }.firstOrNull()

    override val prologStackTrace: List<Struct> by lazy {
        sideEffectManager.logicalParentRequests.asSequence().map { it.query }.toList()
    }

    override fun createSolver(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ) = StreamsSolverFactory.solverOf(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr
    )

    override fun apply(sideEffect: SideEffect): StreamsExecutionContext {
        return super.apply(sideEffect) as StreamsExecutionContext
    }

    override fun apply(sideEffects: Iterable<SideEffect>): StreamsExecutionContext {
        return super.apply(sideEffects) as StreamsExecutionContext
    }

    override fun apply(sideEffects: Sequence<SideEffect>): StreamsExecutionContext {
        return super.apply(sideEffects) as StreamsExecutionContext
    }

    override fun update(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore<*>,
        outputChannels: OutputStore<*>
    ): StreamsExecutionContext {
        return copy(
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels
        )
    }

}

/** Extension method to get [SideEffectManagerImpl], if this context is of right type*/
internal fun ExecutionContext.getSideEffectManager(): SideEffectManagerImpl? =
    (this as? StreamsExecutionContext)?.sideEffectManager
