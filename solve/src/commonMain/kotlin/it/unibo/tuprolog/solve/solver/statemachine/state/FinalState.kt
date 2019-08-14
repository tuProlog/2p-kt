package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Substitution

/**
 * Represents a final state of the Prolog solver state-machine
 *
 * @author Enrico
 */
interface FinalState : State {

    /** The answer substitution computed when reaching a final state */
    val answerSubstitution: Substitution.Unifier
}
