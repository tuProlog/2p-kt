package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.theory.ClauseDatabase

class TestSolverSLD : AbstractSolverTest() {

    override fun solverOf(libraries: Libraries, flags: Map<Atom, Term>, staticKB: ClauseDatabase, dynamicKB: ClauseDatabase): Solver =
            SolverSLD(libraries, flags, staticKB, dynamicKB)

}
