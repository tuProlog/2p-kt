package it.unibo.tuprolog.solve

/**
 * A mutable Prolog Goal solver for probabilistic logic queries which also backwards compatible with simple Prolog
 *
 * @author Jason Dellaluce
 */
interface MutableProbSolver : ProbSolver, MutableSolver {

    companion object {
        // To be extended through extension methods
    }
}
