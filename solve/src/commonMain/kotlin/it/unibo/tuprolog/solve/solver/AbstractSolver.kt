package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.Solver

/**
 * Represent an abstract [Solver]
 *
 * @param startContext The context that will be used on process resolution start
 *
 * @author Enrico
 */
abstract class AbstractSolver(protected open val startContext: ExecutionContextImpl) : Solver
