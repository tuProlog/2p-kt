package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.SolveConcurrentExpectations
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.mutableSolverContract
import it.unibo.tuprolog.solve.solverConstructionContract
import it.unibo.tuprolog.solve.staticFactoryContract

/** The shared solver contracts of `:test-solve`, run against [ConcurrentSolverFactory]. */
class ConcurrentSolverSpec :
    FunSpec({
        include(
            solverConstructionContract<ConcurrentSolver, MutableConcurrentSolver>(
                ConcurrentSolverFactory,
                DefaultBuiltins,
            ),
        )
        include(staticFactoryContract(SolveConcurrentExpectations.expectations))
        include(mutableSolverContract(ConcurrentSolverFactory))
    })
