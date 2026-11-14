package it.unibo.tuprolog.solve.concurrent

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.SolveConcurrentExpectations
import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.goalQueryContract
import it.unibo.tuprolog.solve.metaResolutionContract
import it.unibo.tuprolog.solve.mutableSolverContract
import it.unibo.tuprolog.solve.patternDataContract
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
        include(goalQueryContract(ConcurrentSolverFactory))
        include(
            metaResolutionContract(
                ConcurrentSolverFactory,
                // solutions come in any order, and meta-resolution policies are not supported
                disabled =
                    setOf(
                        "meta-resolution/concrete-call-selects-metarules-in-order",
                        "meta-resolution/enumerate-user-predicates-of-the-arity",
                        "meta-resolution/enumerate-respects-cut-and-negation",
                        "meta-resolution/enumerate-sees-dynamic-predicates",
                        "meta-resolution/enumerate-chain-metarule",
                        "meta-resolution/custom-policy",
                    ),
            ),
        )
        include(patternDataContract(ConcurrentSolverFactory))
    })
