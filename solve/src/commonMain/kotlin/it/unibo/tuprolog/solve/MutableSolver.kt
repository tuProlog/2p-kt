package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.RetractResult
import kotlin.js.JsName

/**
 * A mutable Prolog solver
 */
interface MutableSolver : Solver {

    @JsName("loadLibrary")
    fun loadLibrary(library: AliasedLibrary)

    @JsName("unloadLibrary")
    fun unloadLibrary(library: AliasedLibrary)

    @JsName("setLibraries")
    fun setLibraries(libraries: Libraries)

    @JsName("loadStaticKb")
    fun loadStaticKb(theory: ClauseDatabase)

    @JsName("appendStaticKb")
    fun appendStaticKb(theory: ClauseDatabase)

    @JsName("resetStaticKb")
    fun resetStaticKb()

    @JsName("loadDynamicKb")
    fun loadDynamicKb(theory: ClauseDatabase)

    @JsName("appendDynamicKb")
    fun appendDynamicKb(theory: ClauseDatabase)

    @JsName("resetDynamicKb")
    fun resetDynamicKb()

    @JsName("assertA")
    fun assertA(clause: Clause)

    @JsName("assertAFact")
    fun assertA(fact: Struct)

    @JsName("assertZ")
    fun assertZ(clause: Clause)

    @JsName("assertZFact")
    fun assertZ(fact: Struct)

    @JsName("retract")
    fun retract(clause: Clause): RetractResult

    @JsName("retractByHead")
    fun retract(fact: Struct): RetractResult

    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult

    @JsName("retractAllBeHead")
    fun retractAll(fact: Struct): RetractResult

    companion object {
        // To be extended through extension methods
    }
}
