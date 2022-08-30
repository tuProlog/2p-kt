package it.unibo.tuprolog.solve.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.directives.ClausePartition
import it.unibo.tuprolog.solve.directives.partition
import it.unibo.tuprolog.solve.directives.plus
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.exception.warning.InitializationIssue
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.getAllOperators
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.toOperatorSet
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.buffered

@Suppress("LeakingThis")
abstract class AbstractSolver<E : ExecutionContext>(
    libraries: Runtime = Runtime.empty(),
    flags: FlagStore = FlagStore.empty(),
    initialStaticKb: Theory = Theory.empty(),
    initialDynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore = InputStore.fromStandard(),
    outputChannels: OutputStore = OutputStore.fromStandard(),
    trustKb: Boolean = false
) : Solver {

    protected abstract var currentContext: E

    init {
        currentContext = initializeContext(
            libraries,
            flags,
            initialStaticKb.toImmutableTheory(),
            initialDynamicKb.toMutableTheory(),
            getAllOperators(libraries).toOperatorSet(),
            inputChannels,
            outputChannels,
            trustKb
        )
        if (!trustKb) {
            initializeKb(initialStaticKb, initialDynamicKb)
        }
        onInitialize()
    }

    protected open fun onInitialize() {
        // does nothing by default
    }

    protected abstract fun initializeContext(
        libraries: Runtime = Runtime.empty(),
        flags: FlagStore = FlagStore.empty(),
        staticKb: Theory = Theory.empty(),
        dynamicKb: Theory = MutableTheory.empty(),
        operators: OperatorSet,
        inputChannels: InputStore = InputStore.fromStandard(),
        outputChannels: OutputStore = OutputStore.fromStandard(),
        trustKb: Boolean = false
    ): E

    protected fun updateContext(operator: E.() -> ExecutionContext) {
        @Suppress("UNCHECKED_CAST")
        currentContext = currentContext.operator() as E
    }

    protected fun loadGoal(theory: Atom): Struct = Struct.of("consult", theory)

    private fun resetKb(resetStatic: Boolean, resetDynamic: Boolean) {
        updateContext {
            update(
                staticKb = if (resetStatic) Theory.emptyIndexed() else this.staticKb,
                dynamicKb = if (resetDynamic) MutableTheory.emptyIndexed() else this.dynamicKb
            )
        }
    }

    private fun updateContextWith(clausePartition: ClausePartition) {
        updateContext {
            update(
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
        fun Theory?.isSkippable() =
            this == null || directives.none()

        if (staticKb.isSkippable() && dynamicKb.isSkippable()) return
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
                    warnings.write(InitializationIssue(goal, null, currentContext))
                }
                is Solution.Halt -> {
                    warnings.write(InitializationIssue(goal, solution.exception, solution.exception.contexts))
                }
                else -> {
                    // does nothing
                }
            }
        }
    }

    final override val libraries: Runtime
        get() = currentContext.libraries

    final override val flags: FlagStore
        get() = currentContext.flags

    final override val staticKb: Theory
        get() = currentContext.staticKb

    final override val dynamicKb: Theory
        get() = currentContext.dynamicKb

    final override val inputChannels: InputStore
        get() = currentContext.inputChannels

    final override val outputChannels: OutputStore
        get() = currentContext.outputChannels

    final override val operators: OperatorSet
        get() = currentContext.operators

    final override fun solve(goal: Struct, options: SolveOptions): Sequence<Solution> {
        var solutionSequence = solveImpl(goal, options)
        if (options.limit > 0) {
            solutionSequence = solutionSequence.take(options.limit)
        }
        if (options.isEager) {
            solutionSequence = solutionSequence.buffered()
        }
        return solutionSequence
    }

    protected abstract fun solveImpl(goal: Struct, options: SolveOptions): Sequence<Solution>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AbstractSolver<*>

        if (currentContext != other.currentContext) return false

        return true
    }

    override fun hashCode(): Int {
        return currentContext.hashCode()
    }

    abstract override fun copy(
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): AbstractSolver<E>

    abstract override fun clone(): AbstractSolver<E>

    override fun toString(): String =
        "${this::class.simpleName}(" +
            "libraries=${libraries.aliases}, " +
            "staticKb=${staticKb.clauses.toList()}, " +
            "dynamicKb=${dynamicKb.clauses.toList()}, " +
            "operators=$operators" +
            "flags=$flags, " +
            "inputChannels=$inputChannels, " +
            "outputChannels=$outputChannels, " +
            ")"
}
