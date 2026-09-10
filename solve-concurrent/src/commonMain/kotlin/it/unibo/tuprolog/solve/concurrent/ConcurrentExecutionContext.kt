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

/**
 * The [ExecutionContext] implementation for `:solve-concurrent`: an immutable node of the resolution tree that
 * [it.unibo.tuprolog.solve.concurrent.fsm.State]s carry around and derive from, chained back to the root goal via
 * [parent]. Each concurrently-running coroutine spawned while resolving a goal (see [ConcurrentSolver]) owns its
 * own chain of these, so no synchronization is needed to read [pathToRoot]/[logicStackTrace] from within a single
 * branch -- only knowledge-base mutation shared *across* branches (see [ConcurrentSolver]'s concurrency caveat) is
 * unsynchronized.
 *
 * [depth]/[step]/[parent] together encode the position of this context within the (conceptually tree-shaped, but
 * per-branch linear) resolution: [parent] is `null` exactly at the [isRoot] context (`depth == 0`), and every
 * non-root context has a strictly greater [depth] than its [parent] (enforced by the `init` block). [goals] is the
 * (possibly partially consumed) [Cursor] of remaining conjuncts for [query] at this point in the resolution;
 * [rule]/[primitive] record which clause or primitive response, respectively, is currently being executed to
 * produce the next context down the chain.
 *
 * @property query the original goal this whole activation record chain is trying to prove.
 * @property goals the remaining conjuncts (of [query], or of the current [rule]'s body) still to be resolved.
 * @property rule the clause currently being tried against [it.unibo.tuprolog.solve.concurrent.fsm.State.context]'s
 * current goal, if any.
 * @property primitive the response of the primitive currently being executed, if any.
 * @property parent the context this one was created from (e.g. when descending into a sub-goal), or `null` if
 * this [isRoot].
 * @property depth how many [parent] links separate this context from the root one; `0` for the root context.
 * @property step a monotonically increasing counter of resolution steps taken along this branch, mostly useful for
 * debugging/tracing.
 */
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
    val step: Long = 0,
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    /** Whether this is the root context of the resolution tree, i.e. it has no [parent] (`depth == 0`). */
    val isRoot: Boolean
        get() = depth == 0

    /**
     * Whether this context represents a genuine "activation record" -- i.e. the entry point of a new procedure
     * call -- as opposed to an intermediate context created while unfolding a conjunction/disjunction within the
     * same call. Used to filter [pathToRoot] down to [logicStackTrace].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val isActivationRecord: Boolean
        get() = parent == null || depth - parent.depth >= 1

    /** This context, followed by every [parent], up to (and including) the [isRoot] one. */
    val pathToRoot: Sequence<ConcurrentExecutionContext> =
        sequence {
            var current: ConcurrentExecutionContext? = this@ConcurrentExecutionContext
            while (current != null) {
                yield(current)
                current = current.parent
            }
        }

    /** The first term still to be resolved in [goals], or `null` if [goals] has been fully consumed. */
    val currentGoal: Term?
        get() = if (goals.isOver) null else goals.current

    /**
     * The [Var]s whose bindings must be preserved when [substitution] is filtered/restricted while moving between
     * contexts (e.g. when returning from a sub-goal to its parent): those of [query] at the root, propagated down
     * together with the variables of whatever is left in [goals] at each step.
     */
    val interestingVariables: Set<Var> by lazy {
        val baseInterestingVars: Set<Var> = parent?.interestingVariables ?: query.variables.toSet()
        val currInterestingVars: Set<Var> =
            if (goals.isOver) emptySet() else goals.current?.variables?.toSet() ?: emptySet()

        baseInterestingVars + currInterestingVars
    }

    /** The Prolog call stack trace up to this context, built by walking [pathToRoot] and keeping [isActivationRecord] entries. */
    override val logicStackTrace: KtList<Struct> by lazy {
        pathToRoot
            .filter { it.isActivationRecord }
            .map { it.procedure ?: Struct.of("?-", query) }
            .toList()
    }

    /** Creates a new [ConcurrentSolver], sharing this context's state unless overridden. */
    override fun createSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): ConcurrentSolver =
        ConcurrentSolverImpl(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels,
            trustKb = true,
        )

    /** Creates a new [MutableSolver] backed by this same resolution strategy, sharing this context's state unless overridden. */
    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver =
        MutableConcurrentSolver(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels,
        )

    /** Same as [ExecutionContext.update], but statically typed to return a [ConcurrentExecutionContext]. */
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
    ): ConcurrentExecutionContext =
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

    /** Same as [ExecutionContext.apply], but statically typed to return a [ConcurrentExecutionContext]. */
    override fun apply(sideEffect: SideEffect): ConcurrentExecutionContext =
        super.apply(sideEffect) as ConcurrentExecutionContext

    /** Same as [ExecutionContext.apply], but statically typed to return a [ConcurrentExecutionContext]. */
    override fun apply(sideEffects: Iterable<SideEffect>): ConcurrentExecutionContext =
        super.apply(sideEffects) as ConcurrentExecutionContext

    /** Same as [ExecutionContext.apply], but statically typed to return a [ConcurrentExecutionContext]. */
    override fun apply(sideEffects: Sequence<SideEffect>): ConcurrentExecutionContext =
        super.apply(sideEffects) as ConcurrentExecutionContext

    override fun toString(): String =
        "ConcurrentExecutionContext(" +
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
