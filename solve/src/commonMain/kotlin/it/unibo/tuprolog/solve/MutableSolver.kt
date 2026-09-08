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
 *
 * This is a distinct concern from the state changes a [Solver] performs on itself *while* resolving a single
 * query (e.g. `assertz/1` executed as a goal): those are expressed as
 * [it.unibo.tuprolog.solve.sideffects.SideEffect]s attached to a primitive's response, not through this interface.
 * [MutableSolver] instead lets client code reconfigure a solver from the outside, in between resolutions.
 *
 * Obtain one via [SolverFactory.mutableSolverOf] / [SolverFactory.mutableSolverWithDefaultBuiltins], or
 * [SolverBuilder.buildMutable].
 */
interface MutableSolver : Solver {
    /** Loads [library] into this solver's `Runtime`, adding it to the currently loaded libraries. */
    @JsName("loadLibrary")
    fun loadLibrary(library: Library)

    /** Removes [library] from this solver's `Runtime`. */
    @JsName("unloadLibrary")
    fun unloadLibrary(library: Library)

    /** Replaces this solver's whole `Runtime` of loaded libraries with [libraries]. */
    @JsName("setLibraries")
    fun setRuntime(libraries: Runtime)

    /** Replaces this solver's static knowledge base with [theory]. */
    @JsName("loadStaticKb")
    fun loadStaticKb(theory: Theory)

    /** Replaces this solver's static knowledge base with a [Theory] indexing [clauses]. */
    @JsName("loadStaticClauses")
    fun loadStaticClauses(vararg clauses: Clause) = loadStaticKb(Theory.indexedOf(unificator, *clauses))

    /** Replaces this solver's static knowledge base with a [Theory] indexing [clauses]. */
    @JsName("loadStaticClausesIterable")
    fun loadStaticClauses(clauses: Iterable<Clause>) = loadStaticKb(Theory.indexedOf(unificator, clauses))

    /** Replaces this solver's static knowledge base with a [Theory] indexing [clauses]. */
    @JsName("loadStaticClausesSequence")
    fun loadStaticClauses(clauses: Sequence<Clause>) = loadStaticKb(Theory.indexedOf(unificator, clauses))

    /** Appends [theory]'s clauses to this solver's static knowledge base, keeping the existing ones. */
    @JsName("appendStaticKb")
    fun appendStaticKb(theory: Theory)

    /** Empties this solver's static knowledge base. */
    @JsName("resetStaticKb")
    fun resetStaticKb()

    /** Replaces this solver's dynamic knowledge base with [theory]. */
    @JsName("loadDynamicKb")
    fun loadDynamicKb(theory: Theory)

    /** Replaces this solver's dynamic knowledge base with a [Theory] indexing [clauses]. */
    @JsName("loadDynamicClauses")
    fun loadDynamicClauses(vararg clauses: Clause) = loadDynamicKb(Theory.indexedOf(unificator, *clauses))

    /** Replaces this solver's dynamic knowledge base with a [Theory] indexing [clauses]. */
    @JsName("loadDynamicClausesIterable")
    fun loadDynamicClauses(clauses: Iterable<Clause>) = loadDynamicKb(Theory.indexedOf(unificator, clauses))

    /** Replaces this solver's dynamic knowledge base with a [Theory] indexing [clauses]. */
    @JsName("loadDynamicClausesSequence")
    fun loadDynamicClauses(clauses: Sequence<Clause>) = loadDynamicKb(Theory.indexedOf(unificator, clauses))

    /** Appends [theory]'s clauses to this solver's dynamic knowledge base, keeping the existing ones. */
    @JsName("appendDynamicKb")
    fun appendDynamicKb(theory: Theory)

    /** Empties this solver's dynamic knowledge base. */
    @JsName("resetDynamicKb")
    fun resetDynamicKb()

    /** Prepends [clause] to the dynamic knowledge base, as `asserta/1` would. */
    @JsName("assertA")
    fun assertA(clause: Clause)

    /** Prepends [fact], treated as a fact (a clause with an empty body), to the dynamic knowledge base. */
    @JsName("assertAFact")
    fun assertA(fact: Struct)

    /** Appends [clause] to the dynamic knowledge base, as `assertz/1` would. */
    @JsName("assertZ")
    fun assertZ(clause: Clause)

    /** Appends [fact], treated as a fact (a clause with an empty body), to the dynamic knowledge base. */
    @JsName("assertZFact")
    fun assertZ(fact: Struct)

    /** Removes the first clause in the dynamic knowledge base unifiable with [clause], as `retract/1` would. */
    @JsName("retract")
    fun retract(clause: Clause): RetractResult<Theory>

    /** Removes the first clause in the dynamic knowledge base whose head unifies with [fact]. */
    @JsName("retractByHead")
    fun retract(fact: Struct): RetractResult<Theory>

    /** Removes every clause in the dynamic knowledge base unifiable with [clause], as `retractall/1` would. */
    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult<Theory>

    /** Removes every clause in the dynamic knowledge base whose head unifies with [fact]. */
    @JsName("retractAllBeHead")
    fun retractAll(fact: Struct): RetractResult<Theory>

    /** Sets the Prolog flag named [name] to [value] in this solver's [it.unibo.tuprolog.solve.flags.FlagStore]. */
    @JsName("setFlag")
    fun setFlag(
        name: String,
        value: Term,
    )

    /** Sets the Prolog flag identified by [flag]'s first component to its second component. */
    @JsName("setFlagPair")
    fun setFlag(flag: Pair<String, Term>)

    /** Sets [flag] to its default term value, as reported by `NotableFlag.defaultTerm`. */
    @JsName("setFlagNotable")
    fun setFlag(flag: NotableFlag)

    /** Replaces this solver's standard input channel with [stdIn]. */
    @JsName("setStandardInput")
    fun setStandardInput(stdIn: InputChannel<String>)

    /** Replaces this solver's standard error channel with [stdErr]. */
    @JsName("setStandardError")
    fun setStandardError(stdErr: OutputChannel<String>)

    /** Replaces this solver's standard output channel with [stdOut]. */
    @JsName("setStandardOutput")
    fun setStandardOutput(stdOut: OutputChannel<String>)

    /** Replaces this solver's warnings channel with [warnings]. */
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
        warnings: OutputChannel<Warning>,
    ): MutableSolver

    override fun clone(): MutableSolver

    companion object
}
