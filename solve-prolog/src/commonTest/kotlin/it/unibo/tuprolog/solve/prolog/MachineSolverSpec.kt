package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec

/** The shared solver contracts of `:test-solve`, run against [MachinePrologSolverFactory] (#1010). */
class MachineSolverSpec :
    FunSpec({
        include(prologSolverContracts(MachinePrologSolverFactory))
    })
