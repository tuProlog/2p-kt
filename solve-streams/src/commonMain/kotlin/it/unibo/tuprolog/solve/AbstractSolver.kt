package it.unibo.tuprolog.solve

/**
 * Represent an abstract [Solver]
 *
 * @param startContext The context that will be used on process resolution start
 *
 * @author Enrico
 */
abstract class AbstractSolver(protected open val startContext: ExecutionContext) : Solver
