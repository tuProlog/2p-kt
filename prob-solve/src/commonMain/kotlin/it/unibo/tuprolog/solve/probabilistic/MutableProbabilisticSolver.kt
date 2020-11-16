package it.unibo.tuprolog.solve.probabilistic

import it.unibo.tuprolog.solve.MutableSolver

/**
 * A mutable Probabilistic Logic goal solver based on Prolog and backward compatible with it.
 *
 * @author Jason Dellaluce
 */
interface MutableProbabilisticSolver : ProbabilisticSolver, MutableSolver {

    companion object {
        // To be extended through extension methods
    }
}
