package it.unibo.tuprolog.solve.probabilistic

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

/**
 * This is a direct porting of the SLD FSM implemented in [ClassicSolver], adapted to support probabilistic reasoning.
 * As this implements [Solver], it is completely backwards compatible with classic logic reasoning.
 *
 * TODO: This implementation provides good coverage over the basic features proposed in ProbLog,
 * however the following is still missing:
 *   - Negated goals
 *   - Annotated disjunctions
 *   - Fail rules (Need to be translated into true ones with negated probability)
 *   - BDD construction
 *   - BDD resolution
 *
 * @author Jason Dellaluce
 */
internal open class ClassicProbabilisticSolver(
    libraries: Libraries = Libraries.empty(),
    flags: FlagStore = FlagStore.empty(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore<*> = ExecutionContextAware.defaultInputChannels(),
    outputChannels: OutputStore<*> = ExecutionContextAware.defaultOutputChannels(),
    private val representationFactory: ProbabilisticRepresentationFactory,
    private val prologSolver: Solver,
) : ProbabilisticSolver {

    private var state: State = StateInit(
        ClassicProbabilisticExecutionContext(
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

    protected fun updateContext(contextMapper: ClassicProbabilisticExecutionContext.() -> ClassicProbabilisticExecutionContext) {
        val ctx = state.context
        val newCtx = ctx.contextMapper()
        if (newCtx != ctx) {
            state = state.clone(newCtx)
        }
    }

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        return prologSolver.solve(goal, maxDuration)
    }

    override fun probSolveWorld(goal: Struct, maxDuration: TimeDuration): Sequence<ProbabilisticWorld> {
        val initialContext = ClassicProbabilisticExecutionContext(
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

        return ProbabilisticWorldIterator(state) { newState, newStep ->
            require(newState.context.step == newStep)
            state = newState
        }.asSequence()
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
