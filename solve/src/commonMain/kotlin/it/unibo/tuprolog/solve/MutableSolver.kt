package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * General type for mutable logic [Solver]s.
 * [MutableSolver]s differ from [Solver]s in that they expose public methods for letting clients affect the state
 * of the solver -- e.g.  affecting the KB -- while no resolution process is ongoing.
 */
interface MutableSolver : Solver {

    @JsName("loadLibrary")
    fun loadLibrary(library: Library)

    @JsName("unloadLibrary")
    fun unloadLibrary(library: Library)

    @JsName("setLibraries")
    fun setRuntime(libraries: Runtime)

    @JsName("loadStaticKb")
    fun loadStaticKb(theory: Theory)

    @JsName("loadStaticClauses")
    fun loadStaticClauses(vararg clauses: Clause) =
        loadStaticKb(Theory.indexedOf(unificator, *clauses))

    @JsName("loadStaticClausesIterable")
    fun loadStaticClauses(clauses: Iterable<Clause>) =
        loadStaticKb(Theory.indexedOf(unificator, clauses))

    @JsName("loadStaticClausesSequence")
    fun loadStaticClauses(clauses: Sequence<Clause>) =
        loadStaticKb(Theory.indexedOf(unificator, clauses))

    @JsName("appendStaticKb")
    fun appendStaticKb(theory: Theory)

    @JsName("resetStaticKb")
    fun resetStaticKb()

    @JsName("loadDynamicKb")
    fun loadDynamicKb(theory: Theory)

    @JsName("loadDynamicClauses")
    fun loadDynamicClauses(vararg clauses: Clause) =
        loadDynamicKb(Theory.indexedOf(unificator, *clauses))

    @JsName("loadDynamicClausesIterable")
    fun loadDynamicClauses(clauses: Iterable<Clause>) =
        loadDynamicKb(Theory.indexedOf(unificator, clauses))

    @JsName("loadDynamicClausesSequence")
    fun loadDynamicClauses(clauses: Sequence<Clause>) =
        loadDynamicKb(Theory.indexedOf(unificator, clauses))

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

    @JsName("setStandardInput")
    fun setStandardInput(stdIn: InputChannel<String>)

    @JsName("setStandardError")
    fun setStandardError(stdErr: OutputChannel<String>)

    @JsName("setStandardOutput")
    fun setStandardOutput(stdOut: OutputChannel<String>)

    @JsName("setWarnings")
    fun setWarnings(warnings: OutputChannel<Warning>)

    override fun copy(
        unificator: Unificator,
        libraries: Runtime,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<Warning>
    ): MutableSolver

    override fun clone(): MutableSolver

    companion object
}
