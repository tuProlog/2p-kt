package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.RetractResult

/**
 * A mutable Prolog solver
 */
interface MutableSolver : Solver {

    fun loadLibrary(library: AliasedLibrary)
    fun unloadLibrary(library: AliasedLibrary)
    fun setLibraries(libraries: Libraries)

    fun loadStaticKb(theory: ClauseDatabase)
    fun appendStaticKb(theory: ClauseDatabase)
    fun resetStaticKb()

    fun loadDynamicKb(theory: ClauseDatabase)
    fun appendDynamicKb(theory: ClauseDatabase)
    fun resetDynamicKb()

    fun assertA(clause: Clause)
    fun assertA(fact: Struct)
    fun assertZ(clause: Clause)
    fun assertZ(fact: Struct)

    fun retract(clause: Clause): RetractResult
    fun retract(fact: Struct): RetractResult
    fun retractAll(clause: Clause): RetractResult
    fun retractAll(fact: Struct): RetractResult

    companion object {
        // To be extended through extension methods
    }
}
