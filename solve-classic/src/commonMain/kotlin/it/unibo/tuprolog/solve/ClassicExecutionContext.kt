package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.Cursor
import kotlin.collections.List as KtList
import kotlin.collections.Set as KtSet

data class ClassicExecutionContext(
    override val procedure: Struct? = null,
    override val libraries: Libraries = Libraries(),
    override val flags: PrologFlags = emptyMap(),
    override val staticKb: Theory = Theory.empty(),
    override val dynamicKb: Theory = Theory.empty(),
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
    val choicePoints: ChoicePointContext? = null,
    val parent: ClassicExecutionContext? = null,
    val depth: Int = 0,
    val step: Long = 0
) : ExecutionContext {
    init {
        require((depth == 0 && parent == null) || (depth > 0 && parent != null))
    }

    val isRoot: Boolean
        get() = depth == 0

    val hasOpenAlternatives: Boolean
        get() = choicePoints?.hasOpenAlternatives ?: false

    val isActivationRecord: Boolean
        get() = parent == null || parent.depth == depth - 1

    val pathToRoot: Sequence<ClassicExecutionContext> = sequence {
        var current: ClassicExecutionContext? = this@ClassicExecutionContext
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
        flags: PrologFlags,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): Solver = ClassicSolverFactory.solverOf(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        stdIn,
        stdOut,
        stdErr
    )

    override fun apply(sideEffects: Iterable<SideEffect>): ExecutionContext {
        var dynamicKb = dynamicKb
        var staticKb = staticKb
        var flags = flags
        var libraries = libraries
        var operators = operators
        var inputChannels = inputChannels
        var outputChannels = outputChannels

        for (sideEffect in sideEffects) {
            when (sideEffect) {
                is SideEffect.AddStaticClauses -> {
                    staticKb = if (sideEffect.onTop) {
                        staticKb.assertA(sideEffect.clauses)
                    } else {
                        staticKb.assertZ(sideEffect.clauses)
                    }
                }
                is SideEffect.AddDynamicClauses -> {
                    dynamicKb = if (sideEffect.onTop) {
                        dynamicKb.assertA(sideEffect.clauses)
                    } else {
                        dynamicKb.assertZ(sideEffect.clauses)
                    }
                }
                is SideEffect.ResetStaticKb -> {
                    staticKb = sideEffect.theory
                }
                is SideEffect.ResetDynamicKb -> {
                    dynamicKb = sideEffect.theory
                }
                is SideEffect.RemoveStaticClauses -> {
                    staticKb = staticKb.retract(sideEffect.clauses).theory
                }
                is SideEffect.RemoveDynamicClauses -> {
                    staticKb = staticKb.retract(sideEffect.clauses).theory
                }
                is SideEffect.SetFlags -> {
                    flags = flags + sideEffect.flags
                }
                is SideEffect.ResetFlags -> {
                    flags = sideEffect.flags
                }
                is SideEffect.ClearFlags -> {
                    flags = flags - sideEffect.names
                }
                is SideEffect.LoadLibrary -> {
                    libraries += sideEffect.aliasedLibrary
                }
                is SideEffect.UpdateLibrary -> {
                    libraries = libraries.update(sideEffect.aliasedLibrary)
                }
                is SideEffect.UnloadLibrary -> {
                    libraries -= sideEffect.alias
                }
                is SideEffect.SetOperators -> {
                    operators += sideEffect.operatorSet
                }
                is SideEffect.ResetOperators -> {
                    operators = sideEffect.operatorSet
                }
                is SideEffect.RemoveOperators -> {
                    operators -= sideEffect.operatorSet
                }
                is SideEffect.OpenInputChannels -> {
                    inputChannels = inputChannels + sideEffect.inputChannels
                }
                is SideEffect.ResetInputChannels -> {
                    inputChannels = sideEffect.inputChannels
                }
                is SideEffect.CloseInputChannels -> {
                    inputChannels = inputChannels - sideEffect.names
                }
                is SideEffect.OpenOutputChannels -> {
                    outputChannels = outputChannels + sideEffect.outputChannels
                }
                is SideEffect.ResetOutputChannels -> {
                    outputChannels = sideEffect.outputChannels
                }
                is SideEffect.CloseOutputChannels -> {
                    outputChannels = outputChannels - sideEffect.names
                }
            }
        }
        return copy(
            dynamicKb = dynamicKb,
            staticKb = staticKb,
            flags = flags,
            libraries = libraries,
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels
        )
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
                "operators=${operators.joinToString(",", "{", "}") { "${it.functor}:${it.specifier}" }}, " +
                "inputChannels=${inputChannels.keys}, " +
                "outputChannels=${outputChannels.keys}, " +
                "maxDuration=$maxDuration, " +
                "choicePoints=$choicePoints, " +
                "depth=$depth, " +
                "step=$step" +
                ")"
    }
}