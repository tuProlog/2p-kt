package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

data class ExecutionContext(
        val libraries: Libraries,
        val flags: Map<Atom, Term>,
        val staticKB: ClauseDatabase,
        val dynamicKB: ClauseDatabase
)