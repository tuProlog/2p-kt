package it.unibo.tuprolog.solve

/**
 * A mutable Probabilistic Logic goal solver based on Prolog and backward compatible with it.
 *
 * @author Jason Dellaluce
 */
interface ProbabilisticMutableSolver : ProbabilisticSolver, MutableSolver {

    companion object {
        // To be extended through extension methods
    }
}
