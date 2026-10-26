package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.unifyContract

class PrologUnifySpec :
    FunSpec({
        include(unifyContract(PrologSolverFactory))
    })
