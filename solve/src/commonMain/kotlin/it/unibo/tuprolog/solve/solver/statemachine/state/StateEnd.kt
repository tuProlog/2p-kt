package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Base class of states representing the computation end
 *
 * @author Enrico
 */
internal sealed class StateEnd(override val context: ExecutionContext) : FinalState {

    internal data class True(override val context: ExecutionContext) : StateEnd(context) {
        override fun behave(): State {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class TrueWithChoicePoint(override val context: ExecutionContext) : StateEnd(context) {
        override fun behave(): State {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class False(override val context: ExecutionContext) : StateEnd(context) {
        override fun behave(): State {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    internal data class Halt(override val context: ExecutionContext) : StateEnd(context) {
        override fun behave(): State {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
