package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.probabilistic.fsm.State
import it.unibo.tuprolog.solve.probabilistic.fsm.StateInit
import it.unibo.tuprolog.solve.probabilistic.fsm.clone
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.probabilistic.representation.ProbabilisticRepresentationFactory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

internal open class ClassicProbabilisticSolver(
    libraries: Libraries = Libraries.empty(),
    flags: FlagStore = FlagStore.empty(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore<*> = ExecutionContextAware.defaultInputChannels(),
    outputChannels: OutputStore<*> = ExecutionContextAware.defaultOutputChannels(),
    private val representationFactory: ProbabilisticRepresentationFactory
) : ProbabilisticSolver {

    private var state: State = StateInit(
        ClassicExecutionContext(
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            representationFactory = representationFactory
        )
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
            staticKb = staticKb,
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            maxDuration = maxDuration,
            startTime = currentTimeInstant(),
            representationFactory = representationFactory
        )

        state = StateInit(initialContext)

        return SolutionIterator(state) { newState, newStep ->
            require(newState.context.step == newStep)
            state = newState
        }.asSequence()
    }

    fun probSolveWorld(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticWorld> {
        TODO("Not yet implemented")
    }

    override fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticSolution> {
        TODO("Not yet implemented")
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
