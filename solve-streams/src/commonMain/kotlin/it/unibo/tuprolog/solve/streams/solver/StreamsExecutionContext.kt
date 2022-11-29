package it.unibo.tuprolog.solve.streams.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.streams.SolverStrategies
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/**
 * The execution context implementation for [StreamsSolver]
 *
 * @author Enrico
 */
internal data class StreamsExecutionContext(
    override val unificator: Unificator = Unificator.default,
    override val libraries: Runtime = Runtime.empty(),
    override val flags: FlagStore = FlagStore.empty(),
    override val staticKb: Theory = Theory.empty(unificator),
    override val dynamicKb: Theory = Theory.empty(unificator),
    override val operators: OperatorSet = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
    override val inputChannels: InputStore = InputStore.fromStandard(),
    override val outputChannels: OutputStore = OutputStore.fromStandard(),
    override val customData: CustomDataStore = CustomDataStore.empty(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    override val startTime: TimeInstant = currentTimeInstant(),
    override val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
    /** The key strategies that a solver should use during resolution process */
    val solverStrategies: SolverStrategies = SolverStrategies.prologStandard,
    /** The side effects manager to be used during resolution process */
    val sideEffectManager: SideEffectManagerImpl = SideEffectManagerImpl(),
) : ExecutionContext {

    constructor(context: ExecutionContext, newCurrentSubstitution: Substitution.Unifier) : this( // to be tested
        context.unificator,
        context.libraries,
        context.flags,
        context.staticKb,
        context.dynamicKb,
        context.operators,
        context.inputChannels,
        context.outputChannels,
        context.customData,
        newCurrentSubstitution,
        solverStrategies = (context as? StreamsExecutionContext)?.solverStrategies ?: SolverStrategies.prologStandard,
        sideEffectManager = (context as? StreamsExecutionContext)?.sideEffectManager ?: SideEffectManagerImpl()
    )

    override val procedure: Struct?
        get() = sideEffectManager.logicalParentRequests.map { it.query }.firstOrNull()

    override val logicStackTrace: List<Struct> by lazy {
        sideEffectManager.logicalParentRequests.asSequence().map { it.query }.toList()
    }

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ) = StreamsSolver(unificator, libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels)

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ) = TODO("Not yet implemented")

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
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        customData: CustomDataStore
    ): StreamsExecutionContext {
        return copy(
            unificator = unificator,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb,
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            customData = customData
        )
    }
}

/** Extension method to get [SideEffectManagerImpl], if this context is of right type*/
internal fun ExecutionContext.getSideEffectManager(): SideEffectManagerImpl? =
    (this as? StreamsExecutionContext)?.sideEffectManager
