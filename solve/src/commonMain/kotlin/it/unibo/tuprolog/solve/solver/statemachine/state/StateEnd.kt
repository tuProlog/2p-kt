package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * Base class of states representing the computation end
 *
 * @author Enrico
 */
internal sealed class StateEnd(
        context: ExecutionContext,
        executionScope: CoroutineScope,
        solverStrategies: SolverStrategies
) : AbstractState(context, executionScope, solverStrategies), FinalState {

    internal data class True(
            override val context: ExecutionContext,
            override val executionScope: CoroutineScope,
            override val solverStrategies: SolverStrategies
    ) : StateEnd(context, executionScope, solverStrategies) {

        override fun behave(): Sequence<State> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class TrueWithChoicePoint(
            override val context: ExecutionContext,
            override val executionScope: CoroutineScope,
            override val solverStrategies: SolverStrategies
    ) : StateEnd(context, executionScope, solverStrategies) {

        override fun behave(): Sequence<State> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class False(
            override val context: ExecutionContext,
            override val executionScope: CoroutineScope,
            override val solverStrategies: SolverStrategies
    ) : StateEnd(context, executionScope, solverStrategies) {

        override fun behave(): Sequence<State> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class Halt(
            override val context: ExecutionContext,
            override val executionScope: CoroutineScope,
            override val solverStrategies: SolverStrategies
    ) : StateEnd(context, executionScope, solverStrategies) {

        override fun behave(): Sequence<State> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class Timeout(
            override val context: ExecutionContext,
            override val executionScope: CoroutineScope,
            override val solverStrategies: SolverStrategies
    ) : StateEnd(context, executionScope, solverStrategies) {

        override fun behave(): Sequence<State> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
