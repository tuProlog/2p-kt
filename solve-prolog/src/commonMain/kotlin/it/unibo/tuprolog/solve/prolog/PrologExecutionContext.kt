package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.ResolutionCounters
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.prolog.machine.Cancellation
import it.unibo.tuprolog.solve.search.GoalSelectionPolicy
import it.unibo.tuprolog.solve.search.RuleSelectionPolicy
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.time.Chronometer
import it.unibo.tuprolog.utils.time.ChronometerFactory
import kotlin.time.Duration
import kotlin.collections.List as KtList

/**
 * The world state of a `:solve-prolog` resolution (#1010): knowledge bases, flags, libraries, operators, channels and
 * custom data, plus the resolution's opt-in instruments. It changes only through side effects
 * ([apply]), and the stateful machine (`machine/PrologMachine`) reports each new one to its solver. What a primitive
 * sees is a read-only view of it, with the call's [procedure], [substitution] and [logicStackTrace] (`CallContext`).
 *
 * - [chronometer]: the resolution's chronometer (see [it.unibo.tuprolog.solve.Durable.chronometer]).
 * - [counters]: the resolution's opt-in [ResolutionCounters] (see [it.unibo.tuprolog.solve.SolveOptions.counters]).
 * - [interception], [ruleSelection], [goalSelection]: the resolution's opt-in interceptor and selection policies (see
 *   [it.unibo.tuprolog.solve.SolveOptions]), or `null` for none.
 * - [cancellation]: the [Cancellation] of the `solveStream` this resolution answers or is nested in, or `null`; the
 *   solvers this context creates share it.
 */
internal data class PrologExecutionContext(
    override val procedure: Struct? = null,
    override val unificator: Unificator = Unificator.default,
    override val libraries: Runtime = Runtime.empty(),
    override val flags: FlagStore = FlagStore.empty(),
    override val staticKb: Theory = Theory.empty(unificator),
    override val dynamicKb: MutableTheory = MutableTheory.empty(unificator),
    override val operators: OperatorSet = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
    override val inputChannels: InputStore = InputStore.fromStandard(),
    override val outputChannels: OutputStore = OutputStore.fromStandard(),
    override val customData: CustomDataStore = CustomDataStore.empty(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    override val chronometer: Chronometer = ChronometerFactory.default.create(Duration.INFINITE),
    override val counters: ResolutionCounters? = null,
    val interception: Interception? = null,
    val ruleSelection: RuleSelectionPolicy? = null,
    val goalSelection: GoalSelectionPolicy? = null,
    val cancellation: Cancellation? = null,
) : ExecutionContext {
    override val logicStackTrace: KtList<Struct>
        get() = listOfNotNull(procedure)

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): Solver =
        PrologSolver(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels,
            trustKb = true,
        ).also { it.inheritedCancellation = cancellation }

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver =
        MutablePrologSolver(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels,
            trustKb = true,
        ).also { it.inheritedCancellation = cancellation }

    override fun update(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        customData: CustomDataStore,
    ): PrologExecutionContext =
        copy(
            unificator = unificator,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            customData = customData,
        )

    override fun apply(sideEffect: SideEffect): PrologExecutionContext =
        super.apply(sideEffect) as PrologExecutionContext

    override fun apply(sideEffects: Iterable<SideEffect>): PrologExecutionContext =
        super.apply(sideEffects) as PrologExecutionContext

    override fun apply(sideEffects: Sequence<SideEffect>): PrologExecutionContext =
        super.apply(sideEffects) as PrologExecutionContext

    override fun toString(): String =
        "PrologExecutionContext(unificator=$unificator, chronometer=$chronometer, flags=$flags)"
}
