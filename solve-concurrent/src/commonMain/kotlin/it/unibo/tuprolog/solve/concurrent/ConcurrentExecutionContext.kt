package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
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
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.Cursor
import kotlin.collections.List as KtList

data class ConcurrentExecutionContext(
    override val procedure: Struct? = null,
    override val libraries: Libraries = Libraries.empty(),
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
    val rule: Rule? = null,
    val primitives: Cursor<out Solve.Response> = Cursor.empty(), // todo convert to single Solve.Response
    val startTime: TimeInstant = 0,
    val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
    val parent: ConcurrentExecutionContext? = null,
    val depth: Int = 0,
    val step: Long = 0
) : ExecutionContext {

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

    override val logicStackTrace: KtList<Struct> by lazy {
        pathToRoot.filter { it.isActivationRecord }
            .map { it.procedure ?: Struct.of("?-", query) }
            .toList()
    }

    override fun createSolver(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ): Solver = TODO() // ConcurrentSolver is not a subtype of Solver

    override fun createMutableSolver(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore
    ): MutableSolver = TODO() // ConcurrentSolver is not a subtype of MutableSolver

    override fun update(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore,
        outputChannels: OutputStore,
        customData: CustomDataStore
    ): ConcurrentExecutionContext {
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
            "primitives=$primitives, " +
            "startTime=$startTime, " +
            "operators=${operators.joinToString(",", "{", "}") { "'${it.functor}':${it.specifier}" }}, " +
            "inputChannels=${inputChannels.keys}, " +
            "outputChannels=${outputChannels.keys}, " +
            "maxDuration=$maxDuration, " +
            // "choicePoints=$choicePoints, " +
            "depth=$depth, " +
            "step=$step" +
            ")"
    }
}
