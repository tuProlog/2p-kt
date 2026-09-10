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
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.cached
import kotlin.collections.List as KtList
import kotlin.collections.Set as KtSet

/**
 * One frame of the `:solve-classic` execution-context stack: a substitution, the streams of remaining goals,
 * candidate [Rule]s and candidate primitive responses, plus a [parent] link. Frames chain into their [parent]
 * rather than living in a separate stack container, so the [parent] chain (see [pathToRoot]) *is* the call
 * stack -- ordinary heap-allocated data rather than native JVM/JS stack frames, which is what lets Prolog
 * resolution of arbitrarily deep recursive programs run as an explicit, non-recursive [it.unibo.tuprolog.solve.classic.fsm.State]
 * loop instead of unbounded host-language recursion.
 *
 * Resolving a sub-goal (a primitive call or a rule body) pushes a new context whose [parent] is the current one
 * (see `it.unibo.tuprolog.solve.classic.fsm.createChild`); finishing a context's [goals] pops back to its
 * [parent] in [it.unibo.tuprolog.solve.classic.fsm.StateGoalSelection]. Every instance also threads through
 * [choicePoints], the sibling data structure (see [ChoicePointContext]) recording still-open backtracking
 * alternatives.
 *
 * Most fields mirror [it.unibo.tuprolog.solve.ExecutionContext]; the ones specific to the classic engine are:
 * - [query]/[goals]: the original query, and the cursor over the goals still to be proven in this frame.
 * - [rules]/[primitives]: the still-untried candidate clauses/primitive responses for the current goal, i.e. the
 *   frame's own view of the [choicePoints] entry recorded for it (if any).
 * - [choicePoints]: the head of the choice-point queue as seen from this frame -- see [ChoicePointContext].
 * - [parent]/[depth]: the enclosing frame and this frame's distance from the root (`depth == 0`, [isRoot]).
 * - [step]: a monotonically increasing counter of state-machine transitions, used to keep [ClassicSolver]'s
 *   `currentContext` in sync with resolution progress and to detect [it.unibo.tuprolog.solve.exception.TimeOutException]s.
 * - [relevantVariables]: the [Var]s still of interest to some ancestor frame, used to trim [substitution]s of
 *   variables local to a finished sub-goal when popping back to [parent] (see [isVariableInteresting]).
 *
 * @throws IllegalArgumentException if [depth] and [parent] are inconsistent (`depth == 0` iff `parent == null`),
 * or if [startTime] or [maxDuration] are negative.
 */
data class ClassicExecutionContext(
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
    val rules: Cursor<out Rule> = Cursor.empty(),
    val primitives: Cursor<out Solve.Response> = Cursor.empty(),
    override val startTime: TimeInstant,
    override val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
    val choicePoints: ChoicePointContext? = null,
    val parent: ClassicExecutionContext? = null,
    val depth: Int = 0,
    val step: Long = 0,
    val relevantVariables: KtSet<Var> = emptySet(),
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
        require(startTime >= 0)
        require(maxDuration >= 0)
    }

    /** Whether this is the outermost frame of a resolution (i.e. it has no [parent]). */
    val isRoot: Boolean
        get() = depth == 0

    /** Whether [choicePoints] (or any of its ancestors) still has an alternative to backtrack into. */
    val hasOpenAlternatives: Boolean
        get() = choicePoints?.hasOpenAlternatives ?: false

    /** Whether this frame corresponds to an actual procedure call, i.e. should appear in [logicStackTrace]. */
    @Suppress("MemberVisibilityCanBePrivate")
    val isActivationRecord: Boolean
        get() = parent == null || depth - parent.depth >= 1

    /** This frame and all its ancestors, from here up to (and including) the root, in that order. */
    val pathToRoot: Sequence<ClassicExecutionContext> =
        sequence {
            var current: ClassicExecutionContext? = this@ClassicExecutionContext
            while (current != null) {
                yield(current)
                current = current.parent
            }
        }

    /** The current [goals] entry with [substitution] already applied, or `null` if there is none left to prove. */
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

    /**
     * Whether [variable] is still relevant to this frame or any of its ancestors (i.e. is in [relevantVariables],
     * or occurs in this frame's [currentGoal] or [query], or in an ancestor's). Used when popping a finished
     * frame back to its [parent] (in [it.unibo.tuprolog.solve.classic.fsm.StateGoalSelection]) to filter the
     * [substitution] handed up, discarding bindings for variables local to the finished sub-goal.
     */
    fun isVariableInteresting(variable: Var) = variable in interestingVariables

    override val logicStackTrace: KtList<Struct> by lazy {
        pathToRoot
            .filter { it.isActivationRecord }
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
        outputChannels: OutputStore,
    ): Solver =
        ClassicSolver(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels,
            trustKb = true,
        )

    override fun createMutableSolver(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        inputChannels: InputStore,
        outputChannels: OutputStore,
    ): MutableSolver =
        MutableClassicSolver(
            unificator,
            libraries,
            flags,
            staticKb,
            dynamicKb,
            inputChannels,
            outputChannels,
            trustKb = true,
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
        customData: CustomDataStore,
    ): ClassicExecutionContext =
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

    override fun apply(sideEffect: SideEffect): ClassicExecutionContext =
        super.apply(sideEffect) as ClassicExecutionContext

    override fun apply(sideEffects: Iterable<SideEffect>): ClassicExecutionContext =
        super.apply(sideEffects) as ClassicExecutionContext

    override fun apply(sideEffects: Sequence<SideEffect>): ClassicExecutionContext =
        super.apply(sideEffects) as ClassicExecutionContext

    override fun toString(): String =
        "ClassicExecutionContext(" +
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
            "unificator=$unificator, " +
            "choicePoints=$choicePoints" +
            ")"
}
