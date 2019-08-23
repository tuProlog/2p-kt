package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Interface representing internal strategies used by the [Solver]
 *
 * @author Enrico
 */
interface SolverStrategies {

    /** Selects the predication to be solved first, from provided sequence */
    fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>, context: ExecutionContext): P

    /** Selects the clause to be expanded in place of unifying predication, from provided sequence */
    fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>, context: ExecutionContext): C

    /** Determines "when and what" is considered successfully demonstrated, during solution process */
    fun successCheckStrategy(term: Term, context: ExecutionContext): Boolean

    companion object {

        /** Returns a strategy object containing the key solution strategies used by a Standard Prolog solver */
        val prologStandard = object : SolverStrategies {

            /** The default implementation, following the Prolog Standard, selects the first predication found */
            override fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>, context: ExecutionContext): P =
                    predicationSequence.first()

            /** The default implementation, following the Prolog Standard, selects the first clause found */
            override fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>, context: ExecutionContext): C =
                    unifiableClauses.first()

            override fun successCheckStrategy(term: Term, context: ExecutionContext): Boolean = term.isTrue
        }
    }
}
