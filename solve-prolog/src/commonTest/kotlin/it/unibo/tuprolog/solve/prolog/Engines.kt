package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory

/** This module's engines, by the name tests prefix their cases with (#1010). */
internal val engines: List<Pair<String, SolverFactory>> =
    listOf("fsm" to PrologSolverFactory, "machine" to MachinePrologSolverFactory)
