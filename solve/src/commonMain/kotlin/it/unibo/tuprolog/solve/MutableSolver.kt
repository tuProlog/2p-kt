package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * General type for mutable logic [Solver]s.
 * [MutableSolver]s differ from [Solver]s in that they expose public methods for letting clients affect the state
 * of the solver -- e.g.  affecting the KB -- while no resolution process is ongoing.
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

    @JsName("setStandardInput")
    fun setStandardInput(stdIn: InputChannel<String>)

    @JsName("setStandardError")
    fun setStandardError(stdErr: OutputChannel<String>)

    @JsName("setStandardOutput")
    fun setStandardOutput(stdOut: OutputChannel<String>)

    @JsName("setWarnings")
    fun setWarnings(warnings: OutputChannel<PrologWarning>)

    override fun copy(
        libraries: Libraries,
        flags: FlagStore,
        staticKb: Theory,
        dynamicKb: Theory,
        stdIn: InputChannel<String>,
        stdOut: OutputChannel<String>,
        stdErr: OutputChannel<String>,
        warnings: OutputChannel<PrologWarning>
    ): MutableSolver

    override fun clone(): MutableSolver

    companion object {
        @JvmStatic
        @JsName("classic")
        val classic: SolverFactory by lazy {
            classicSolverFactory()
        }

        @JvmStatic
        @JsName("streams")
        val streams: SolverFactory by lazy {
            streamsSolverFactory()
        }
    }
}
