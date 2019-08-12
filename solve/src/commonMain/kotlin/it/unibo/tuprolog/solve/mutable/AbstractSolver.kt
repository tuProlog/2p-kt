package it.unibo.tuprolog.solve.mutable

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term

/**
 * Represent an abstract [Solver]
 *
 * @author Enrico
 */
abstract class AbstractSolver : Solver {

    /**
     * Selects the predication to be solved first, from provided sequence
     *
     * The default implementation, following the Prolog Standard, selects the first predication found
     */
    protected open fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>): P =
            predicationSequence.first()

    /**
     * Selects the clause to be expanded in place of unifying predication, from provided sequence
     *
     * The default implementation, following the Prolog Standard, selects the first clause found
     */
    protected open fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>): C =
            unifiableClauses.first()

    /** Determines "when and what" is considered successfully demonstrated, during solution process */
    protected open fun successCheckStrategy(term: Term): Boolean = term.isTrue

}
