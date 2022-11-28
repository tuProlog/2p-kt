package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.TimeInstant
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cursor
import kotlin.collections.List as KtList

data class ConcurrentExecutionContext(
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
    val query: Struct = Truth.TRUE,
    val goals: Cursor<out Term> = Cursor.empty(),
    val rule: Rule? = null,
    val primitive: Solve.Response? = null,
    override val startTime: TimeInstant,
    override val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
    val parent: ConcurrentExecutionContext? = null,
    val depth: Int = 0,
    val step: Long = 0
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    val isRoot: Boolean
        get() = depth == 0

    @Suppress("MemberVisibilityCanBePrivate")
    val isActivationRecord: Boolean
        get() = parent == null || depth - parent.depth >= 1

    val pathToRoot: Sequence<ConcurrentExecutionContext> = sequence {
        var current: ConcurrentExecutionContext? = this@ConcurrentExecutionContext
        while (current != null) {
            yield(current)
            current = current.parent
        }
    }

    val currentGoal: Term?
        get() = if (goals.isOver) null else goals.current

    val interestingVariables: Set<Var> by lazy {
        val baseInterestingVars: Set<Var> = parent?.interestingVariables ?: query.variables.toSet()
        val currInterestingVars: Set<Var> =
            if (goals.isOver) emptySet() else goals.current?.variables?.toSet() ?: emptySet()

        baseInterestingVars + currInterestingVars
    }

    override val logicStackTrace: KtList<Struct> by lazy {
        pathToRoot.filter { it.isActivationRecord }
            .map { it.procedure ?: Struct.of("?-", query) }
            .toList()
    }

    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ): ConcurrentSolver = ConcurrentSolverImpl(
        unificator,
        libraries,
        flags,
        staticKb,
        dynamicKb,
        inputChannels,
        outputChannels,
        trustKb = true
    )

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ): MutableSolver = MutableConcurrentSolver(
        unificator,
        libraries,
        flags,
        staticKb,
        dynamicKb,
        inputChannels,
        outputChannels
    )

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
    ): ConcurrentExecutionContext {
        return copy(
            unificator = unificator,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            customData = customData
        )
    }

    override fun apply(sideEffect: SideEffect): ConcurrentExecutionContext {
        return super.apply(sideEffect) as ConcurrentExecutionContext
    }

    override fun apply(sideEffects: Iterable<SideEffect>): ConcurrentExecutionContext {
        return super.apply(sideEffects) as ConcurrentExecutionContext
    }

    override fun apply(sideEffects: Sequence<SideEffect>): ConcurrentExecutionContext {
        return super.apply(sideEffects) as ConcurrentExecutionContext
    }

    override fun toString(): String {
        return "ConcurrentExecutionContext(" +
            "query=$query, " +
            "procedure=$procedure, " +
            "substitution=$substitution, " +
            "goals=$goals, " +
            "rules=$rule, " +
            "primitives=$primitive, " +
            "startTime=$startTime, " +
            "operators=${operators.joinToString(",", "{", "}") { "'${it.functor}':${it.specifier}" }}, " +
            "inputChannels=${inputChannels.keys}, " +
            "outputChannels=${outputChannels.keys}, " +
            "maxDuration=$maxDuration, " +
            "depth=$depth, " +
            "step=$step" +
            ")"
    }
}
