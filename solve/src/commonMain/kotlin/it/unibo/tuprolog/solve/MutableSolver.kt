package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
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
    fun loadStaticKb(theory: Theory)

    @JsName("loadStaticClauses")
    fun loadStaticClauses(vararg clauses: Clause) =
        loadStaticKb(Theory.of(*clauses))

    @JsName("loadStaticClausesIterable")
    fun loadStaticClauses(clauses: Iterable<Clause>) =
        loadStaticKb(Theory.of(clauses))

    @JsName("loadStaticClausesSequence")
    fun loadStaticClauses(clauses: Sequence<Clause>) =
        loadStaticKb(Theory.of(clauses))

    @JsName("appendStaticKb")
    fun appendStaticKb(theory: Theory)

    @JsName("resetStaticKb")
    fun resetStaticKb()

    @JsName("loadDynamicKb")
    fun loadDynamicKb(theory: Theory)

    @JsName("loadDynamicClauses")
    fun loadDynamicClauses(vararg clauses: Clause) =
        loadDynamicKb(Theory.of(*clauses))

    @JsName("loadDynamicClausesIterable")
    fun loadDynamicClauses(clauses: Iterable<Clause>) =
        loadDynamicKb(Theory.of(clauses))

    @JsName("loadDynamicClausesSequence")
    fun loadDynamicClauses(clauses: Sequence<Clause>) =
        loadDynamicKb(Theory.of(clauses))

    @JsName("appendDynamicKb")
    fun appendDynamicKb(theory: Theory)

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
    fun retract(clause: Clause): RetractResult<Theory>

    @JsName("retractByHead")
    fun retract(fact: Struct): RetractResult<Theory>

    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult<Theory>

    @JsName("retractAllBeHead")
    fun retractAll(fact: Struct): RetractResult<Theory>

    @JsName("setFlag")
    fun setFlag(name: String, value: Term)

    @JsName("setFlagPair")
    fun setFlag(flag: Pair<String, Term>)

    @JsName("setFlagNotable")
    fun setFlag(flag: NotableFlag)

    companion object {
        // To be extended through extension methods
    }
}
