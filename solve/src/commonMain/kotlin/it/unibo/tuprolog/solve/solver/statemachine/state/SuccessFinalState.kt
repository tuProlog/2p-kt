package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Substitution

/**
 * Represents a final successful state of the Prolog solver state-machine
 *
 * @author Enrico
 */
interface SuccessFinalState : FinalState {

    /** The answer substitution computed when reaching a success final state */
    val answerSubstitution: Substitution.Unifier
}
