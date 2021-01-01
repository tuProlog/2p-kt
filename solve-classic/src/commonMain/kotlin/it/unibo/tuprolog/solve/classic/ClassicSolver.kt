package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.InputStore
import it.unibo.tuprolog.solve.OutputStore
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.fsm.StateInit
import it.unibo.tuprolog.solve.classic.fsm.clone
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.directives.ClausePartition
import it.unibo.tuprolog.solve.directives.partition
import it.unibo.tuprolog.solve.directives.plus
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.exception.warning.InitializationIssue
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

internal open class ClassicSolver : Solver {

    private var state: State

    constructor(
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        initialStaticKb: Theory = Theory.empty(),
        initialDynamicKb: Theory = MutableTheory.empty(),
        inputChannels: InputStore<*> = ExecutionContextAware.defaultInputChannels(),
        outputChannels: OutputStore<*> = ExecutionContextAware.defaultOutputChannels(),
        trustKb: Boolean = false
    ) {
        if (trustKb) {
            state = StateInit(
                ClassicExecutionContext(
                    libraries = libraries,
                    flags = flags,
                    staticKb = initialStaticKb.toImmutableTheory(),
                    dynamicKb = initialDynamicKb.toMutableTheory(),
                    operators = getAllOperators(libraries).toOperatorSet(),
                    inputChannels = inputChannels,
                    outputChannels = outputChannels
                )
            )
        } else {
            state = StateInit(
                ClassicExecutionContext(
                    libraries = libraries,
                    flags = flags,
                    staticKb = Theory.emptyIndexed(),
                    dynamicKb = MutableTheory.emptyIndexed(),
                    operators = getAllOperators(libraries).toOperatorSet(),
                    inputChannels = inputChannels,
                    outputChannels = outputChannels
                )
            )
            initializeKb(initialStaticKb, initialDynamicKb)
        }
    }

    constructor(
        libraries: Libraries = Libraries.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        stdIn: InputChannel<String> = InputChannel.stdIn(),
        stdOut: OutputChannel<String> = OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = OutputChannel.stdErr(),
        warnings: OutputChannel<PrologWarning> = OutputChannel.warn(),
        trustKb: Boolean = false
    ) : this(
        libraries,
        flags,
        staticKb,
        dynamicKb,
        mapOf(ExecutionContextAware.STDIN to stdIn),
        mapOf(
            ExecutionContextAware.STDOUT to stdOut,
            ExecutionContextAware.STDERR to stdErr,
            ExecutionContextAware.WARNINGS to warnings
        ),
        trustKb
    )

    protected fun updateContext(contextMapper: ClassicExecutionContext.() -> ClassicExecutionContext) {
        val ctx = state.context
        val newCtx = ctx.contextMapper()
        if (newCtx != ctx) {
            state = state.clone(newCtx)
        }
    }

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        val initialContext = ClassicExecutionContext(
            query = goal,
            libraries = libraries,
            flags = flags,
            staticKb = staticKb.toImmutableTheory(),
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            maxDuration = maxDuration,
            startTime = currentTimeInstant()
        )

        state = StateInit(initialContext)

        return SolutionIterator(state) { newState, newStep ->
            require(newState.context.step == newStep)
            state = newState
        }.asSequence()
    }

    private fun loadGoal(theory: Atom): Struct = Struct.of("consult", theory)

    private fun resetKb(resetStatic: Boolean, resetDynamic: Boolean) {
        updateContext {
            copy(
                staticKb = if (resetStatic) Theory.emptyIndexed() else this.staticKb,
                dynamicKb = if (resetDynamic) MutableTheory.emptyIndexed() else this.dynamicKb
            )
        }
    }

    private fun updateContextWith(clausePartition: ClausePartition) {
        updateContext {
            copy(
                staticKb = (this.staticKb + clausePartition.staticClauses).toImmutableTheory(),
                dynamicKb = (this.dynamicKb + clausePartition.dynamicClauses).toMutableTheory(),
                operators = operators + clausePartition.operators,
                flags = flags + clausePartition.flagStore
            )
        }
    }

    protected fun initializeKb(
        staticKb: Theory? = null,
        dynamicKb: Theory? = null,
        appendStatic: Boolean = true,
        appendDynamic: Boolean = true
    ) {
        if (staticKb.let { it == null || it.size == 0L } && dynamicKb.let { it == null || it.size == 0L }) return
        val staticKbPartitioning = staticKb?.partition()
        val dynamicKbPartitioning = dynamicKb?.partition(staticByDefault = false)
        val merged = staticKbPartitioning + dynamicKbPartitioning
        resetKb(!appendStatic, !appendDynamic)
        merged.includes.map { loadGoal(it) }.forEach(this::solveInitialGoal)
        updateContextWith(merged)
        merged.initialGoals.forEach(this::solveInitialGoal)
    }

    private fun solveInitialGoal(goal: Struct) {
        for (solution in solve(goal)) {
            when (solution) {
                is Solution.No -> {
                    warnings?.write(InitializationIssue(goal, null, state.context))
                }
                is Solution.Halt -> {
                    warnings?.write(InitializationIssue(goal, solution.exception, solution.exception.contexts))
                }
            }
        }
    }

    override val libraries: Libraries
        get() = state.context.libraries

    override val flags: FlagStore
        get() = state.context.flags

    override val staticKb: Theory
        get() = state.context.staticKb

    override val dynamicKb: Theory
        get() = state.context.dynamicKb

    override val inputChannels: InputStore<*>
        get() = state.context.inputChannels

    override val outputChannels: OutputStore<*>
        get() = state.context.outputChannels

    override val operators: OperatorSet
        get() = state.context.operators
}
