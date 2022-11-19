package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.MutableSolver
import it.unibo.tuprolog.solve.Solver
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
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cached
import kotlin.collections.List as KtList
import kotlin.collections.Set as KtSet

data class ClassicExecutionContext(
    override val procedure: Struct? = null,
    override val libraries: Runtime = Runtime.empty(),
    override val flags: FlagStore = FlagStore.empty(),
    override val staticKb: Theory = Theory.empty(),
    override val dynamicKb: MutableTheory = MutableTheory.empty(),
    override val operators: OperatorSet = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
    override val inputChannels: InputStore = InputStore.fromStandard(),
    override val outputChannels: OutputStore = OutputStore.fromStandard(),
    override val customData: CustomDataStore = CustomDataStore.empty(),
    override val substitution: Substitution.Unifier = Substitution.empty(),
    val query: Struct = Truth.TRUE,
    val goals: Cursor<out Term> = Cursor.empty(),
    val rules: Cursor<out Rule> = Cursor.empty(),
    val primitives: Cursor<out Solve.Response> = Cursor.empty(),
    override val startTime: TimeInstant,
    override val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
    val choicePoints: ChoicePointContext? = null,
    val parent: ClassicExecutionContext? = null,
    val depth: Int = 0,
    val step: Long = 0,
    val relevantVariables: KtSet<Var> = emptySet()
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
        require(startTime >= 0)
        require(maxDuration >= 0)
    }

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = choicePoints?.hasOpenAlternatives ?: false

    @Suppress("MemberVisibilityCanBePrivate")
    val isActivationRecord: Boolean
        get() = parent == null || depth - parent.depth >= 1

    val pathToRoot: Sequence<ClassicExecutionContext> = sequence {
        var current: ClassicExecutionContext? = this@ClassicExecutionContext
        while (current != null) {
            yield(current)
            current = current.parent
        }
    }

    val currentGoal: Term? by lazy {
        if (goals.isOver) null else goals.current?.apply(substitution)
    }

    private val locallyInterestingVariables: Sequence<Var>
        get() = relevantVariables.asSequence() + (goals.current?.variables ?: emptySequence())

    private val interestingVariables: Sequence<Var> by lazy {
        (locallyInterestingVariables + query.variables + pathToRoot.flatMap { it.locallyInterestingVariables })
            .distinct()
            .cached()
    }

    fun isVariableInteresting(variable: Var) =
        variable in interestingVariables

    override val logicStackTrace: KtList<Struct> by lazy {
        pathToRoot.filter { it.isActivationRecord }
            .map { it.procedure ?: Struct.of("?-", query) }
            .toList()
    }

    override fun createSolver(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ): Solver = ClassicSolver(libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels, trustKb = true)

    override fun createMutableSolver(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ): MutableSolver =
        MutableClassicSolver(libraries, flags, staticKb, dynamicKb, inputChannels, outputChannels, trustKb = true)

    override fun update(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        customData: CustomDataStore
    ): ClassicExecutionContext {
        return copy(
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

    override fun apply(sideEffect: SideEffect): ClassicExecutionContext {
        return super.apply(sideEffect) as ClassicExecutionContext
    }

    override fun apply(sideEffects: Iterable<SideEffect>): ClassicExecutionContext {
        return super.apply(sideEffects) as ClassicExecutionContext
    }

    override fun apply(sideEffects: Sequence<SideEffect>): ClassicExecutionContext {
        return super.apply(sideEffects) as ClassicExecutionContext
    }

    override fun toString(): String {
        return "ClassicExecutionContext(" +
            "step=$step, " +
            "depth=$depth, " +
            "substitution=$substitution, " +
            "logicStackTrace=$logicStackTrace, " +
            "goals=$goals, " +
            "rules=$rules, " +
            "primitives=$primitives, " +
            "startTime=$startTime, " +
            "endTime=$endTime, " +
            "maxDuration=$maxDuration, " +
            "choicePoints=$choicePoints" +
            ")"
    }
}
