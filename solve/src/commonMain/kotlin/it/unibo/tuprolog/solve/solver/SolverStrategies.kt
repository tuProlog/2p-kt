package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term

/** Interface representing internal strategies used by the Solver */
interface SolverStrategies {

    /** Selects the predication to be solved first, from provided sequence */
    fun <P : Term> predicationChoiceStrategy(predicationSequence: Sequence<P>): P

    /** Selects the clause to be expanded in place of unifying predication, from provided sequence */
    fun <C : Clause> clauseChoiceStrategy(unifiableClauses: Sequence<C>): C

    /** Determines "when and what" is considered successfully demonstrated, during solution process */
    fun successCheckStrategy(term: Term): Boolean
}
