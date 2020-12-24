package it.unibo.tuprolog.solve

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext
import it.unibo.tuprolog.solve.problogclassic.fsm.State
import it.unibo.tuprolog.solve.problogclassic.fsm.StateInit
import it.unibo.tuprolog.solve.problogclassic.fsm.clone
import it.unibo.tuprolog.solve.problogclassic.knowledge.ProblogSolutionTerm
import it.unibo.tuprolog.solve.problogclassic.probability
import it.unibo.tuprolog.solve.problogclassic.problogOr
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory

internal open class ProblogClassicSolver(
    libraries: Libraries = Libraries.empty(),
    flags: FlagStore = FlagStore.empty(),
    staticKb: Theory = Theory.empty(),
    dynamicKb: Theory = MutableTheory.empty(),
    inputChannels: InputStore<*> = ExecutionContextAware.defaultInputChannels(),
    outputChannels: OutputStore<*> = ExecutionContextAware.defaultOutputChannels()
) : ProbSolver {

    private fun probMapSolutionGroup(
        group: List<Pair<Solution, BinaryDecisionDiagram<ProblogSolutionTerm>>>
    ): ProbSolution {
        var solutionBDD: BinaryDecisionDiagram<ProblogSolutionTerm> = BinaryDecisionDiagram.Terminal(false)
        group.forEach {
            solutionBDD = solutionBDD problogOr it.second
        }
        return ProbSolution(group[0].first, solutionBDD.probability())
    }

    override fun probSolve(goal: Struct, maxDuration: TimeDuration): Sequence<ProbSolution> {
        return solveInternal(goal, maxDuration).groupBy { it.first.substitution }.map {
            probMapSolutionGroup(it.value)
        }.asSequence()
    }

    override fun solve(goal: Struct, maxDuration: TimeDuration): Sequence<Solution> {
        // TODO: Switch back to normal solving
        return probSolve(goal, maxDuration).map { it.asSolution() }
    }

    private var state: State = StateInit(
        ProblogClassicExecutionContext(
            libraries = libraries,
            flags = flags,
            staticKb = staticKb,
            dynamicKb = dynamicKb.toMutableTheory(),
            operators = getAllOperators(libraries, staticKb, dynamicKb).toOperatorSet(),
            inputChannels = inputChannels,
            outputChannels = outputChannels
        )
    )

    protected fun updateContext(contextMapper: ProblogClassicExecutionContext.() -> ProblogClassicExecutionContext) {
        val ctx = state.context
        val newCtx = ctx.contextMapper()
        if (newCtx != ctx) {
            state = state.clone(newCtx)
        }
    }

    private fun solveInternal(goal: Struct, maxDuration: TimeDuration):
        Sequence<Pair<Solution, BinaryDecisionDiagram<ProblogSolutionTerm>>> {
            val initialContext = ProblogClassicExecutionContext(
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

            return ProblogClassicSolutionIterator(state) { newState, newStep ->
                require(newState.context.step == newStep)
                state = newState
            }.asSequence()
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
