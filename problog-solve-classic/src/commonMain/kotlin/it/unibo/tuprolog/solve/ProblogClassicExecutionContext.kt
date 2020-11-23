package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.problogimpl.ProblogFact
import it.unibo.tuprolog.solve.problogimpl.ProblogRule
import it.unibo.tuprolog.struct.BinaryDecisionDiagram
import it.unibo.tuprolog.struct.impl.ofTrueTerminal
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.Cursor
import kotlin.collections.List as KtList
import kotlin.collections.Set as KtSet

data class ProblogClassicExecutionContext(
        override val procedure: Struct? = null,
        override val libraries: Libraries = Libraries.empty(),
        override val flags: FlagStore = FlagStore.empty(),
        override val staticKb: Theory = Theory.empty(),
        override val dynamicKb: MutableTheory = MutableTheory.empty(),
        override val operators: OperatorSet = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
        override val inputChannels: Map<String, InputChannel<*>> = ExecutionContextAware.defaultInputChannels(),
        override val outputChannels: Map<String, OutputChannel<*>> = ExecutionContextAware.defaultOutputChannels(),
        override val substitution: Substitution.Unifier = Substitution.empty(),
        val query: Struct = Truth.TRUE,
        val goals: Cursor<out Term> = Cursor.empty(),
        val rules: Cursor<out Rule> = Cursor.empty(),
        val primitives: Cursor<out Solve.Response> = Cursor.empty(),
        val startTime: TimeInstant = 0,
        val maxDuration: TimeDuration = TimeDuration.MAX_VALUE,
        val choicePoints: ProblogChoicePointContext? = null,
        val parent: ProblogClassicExecutionContext? = null,
        val depth: Int = 0,
        val step: Long = 0,
        val probRule: ProblogRule? = null,
        val bdd: BinaryDecisionDiagram<ProblogFact> = BinaryDecisionDiagram.ofTrueTerminal()
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = choicePoints?.hasOpenAlternatives ?: false

    @Suppress("MemberVisibilityCanBePrivate")
    val isActivationRecord: Boolean
        get() = parent == null || parent.depth == depth - 1

    val pathToRoot: Sequence<ProblogClassicExecutionContext> = sequence {
        var current: ProblogClassicExecutionContext? = this@ProblogClassicExecutionContext
        while (current != null) {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            yield(current!!)
            current = current.parent
        }
    }

    val currentGoal: Term?
        get() = if (goals.isOver) null else goals.current

    val interestingVariables: KtSet<Var> by lazy {
        val baseInterestingVars: KtSet<Var> = parent?.interestingVariables ?: query.variables.toSet()
        val currInterestingVars: KtSet<Var> =
            if (goals.isOver) emptySet() else goals.current?.variables?.toSet() ?: emptySet()

        baseInterestingVars + currInterestingVars
    }

    override val prologStackTrace: KtList<Struct> by lazy {
        pathToRoot.filter { it.isActivationRecord }
            .map { it.procedure ?: Struct.of("?-", query) }
            .toList()
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
    ): Solver = ProblogClassicSolverFactory.solverOf(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr
    )

    override fun update(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        operators: OperatorSet,
        inputChannels: InputStore<*>,
        outputChannels: OutputStore<*>
    ): ProblogClassicExecutionContext {
        return copy(
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
        )
    }

    override fun apply(sideEffect: SideEffect): ProblogClassicExecutionContext {
        return super.apply(sideEffect) as ProblogClassicExecutionContext
    }

    override fun apply(sideEffects: Iterable<SideEffect>): ProblogClassicExecutionContext {
        return super.apply(sideEffects) as ProblogClassicExecutionContext
    }

    override fun apply(sideEffects: Sequence<SideEffect>): ProblogClassicExecutionContext {
        return super.apply(sideEffects) as ProblogClassicExecutionContext
    }

    override fun toString(): String {
        return "ClassicExecutionContext(" +
            "query=$query, " +
            "procedure=$procedure, " +
            "substitution=$substitution, " +
            "goals=$goals, " +
            "rules=$rules, " +
            "primitives=$primitives, " +
            "startTime=$startTime, " +
            "operators=${operators.joinToString(",", "{", "}") { "'${it.functor}':${it.specifier}" }}, " +
            "inputChannels=${inputChannels.keys}, " +
            "outputChannels=${outputChannels.keys}, " +
            "maxDuration=$maxDuration, " +
            "choicePoints=$choicePoints, " +
            "depth=$depth, " +
            "step=$step" +
            ")"
    }
}
