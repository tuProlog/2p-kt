package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.FlagStore
import it.unibo.tuprolog.solve.InputStore
import it.unibo.tuprolog.solve.OutputStore
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.classic.fsm.State
import it.unibo.tuprolog.solve.classic.fsm.StateInit
import it.unibo.tuprolog.solve.classic.fsm.clone
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.directives.partition
import it.unibo.tuprolog.solve.exception.warning.InitializationIssue
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

internal open class ClassicSolver(
    libraries: Libraries = Libraries.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(),
    initialDynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore<*> = ExecutionContextAware.defaultInputChannels(),
    outputChannels: OutputStore<*> = ExecutionContextAware.defaultOutputChannels()
) : Solver {

    private var state: State = StateInit(
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

    init {
        initializeKb(initialStaticKb, initialDynamicKb)
    }

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
            staticKb = staticKb,
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

    protected fun initializeKb(staticKb: Theory?, dynamicKb: Theory?) {
        val staticKbPartitioning = staticKb?.partition()
        val dynamicKbPartitioning = dynamicKb?.partition(staticByDefault = false)
        val newStaticKb = Theory.indexedOf(
            sequenceOf(staticKbPartitioning, dynamicKbPartitioning)
                .filterNotNull()
                .flatMap { it.staticClauses.asSequence() }
        )
        val newDynamicKb = MutableTheory.indexedOf(
            sequenceOf(staticKbPartitioning, dynamicKbPartitioning)
                .filterNotNull()
                .flatMap { it.dynamicClauses.asSequence() }
        )
        val newOperators = sequenceOf(staticKbPartitioning, dynamicKbPartitioning)
            .filterNotNull()
            .map { it.operators }
            .reduce(OperatorSet::plus)
        val newFlags = sequenceOf(staticKbPartitioning, dynamicKbPartitioning)
            .filterNotNull()
            .map { it.flagStore }
            .reduce(FlagStore::plus)
        val includes = sequenceOf(staticKbPartitioning, dynamicKbPartitioning)
            .filterNotNull()
            .flatMap { it.includes }
            .map { Struct.of("consult", it) }
        includes.forEach(this::solveInitialGoal)
        updateContext {
            copy(
                staticKb = this.staticKb + newStaticKb,
                dynamicKb = this.dynamicKb + newDynamicKb,
                operators = operators + newOperators,
                flags = flags + newFlags
            )
        }
        val initialGoals = sequenceOf(staticKbPartitioning, dynamicKbPartitioning)
            .filterNotNull()
            .flatMap { it.initialGoals }
        initialGoals.forEach(this::solveInitialGoal)
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
