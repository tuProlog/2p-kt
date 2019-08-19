package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Represent an abstract [Solver]
 *
 * @author Enrico
 */
abstract class AbstractSolver : Solver {

    /**
     * Returns a strategy object containing the key solution strategies used by this solver
     *
     * Default implementation returns a strategy object that follows the Prolog Standard
     */
    protected open val solverStrategies = object : SolverStrategies {

        /** The default implementation, following the Prolog Standard, selects the first predication found */
        override fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>, context: ExecutionContext): P =
                predicationSequence.first()

        /** The default implementation, following the Prolog Standard, selects the first clause found */
        override fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>, context: ExecutionContext): C =
                unifiableClauses.first()

        override fun successCheckStrategy(term: Term, context: ExecutionContext): Boolean = term.isTrue
    }
}
