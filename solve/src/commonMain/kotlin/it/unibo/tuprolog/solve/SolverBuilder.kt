package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.NotableFlag
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface SolverBuilder {

    @JsName("toFactory")
    fun toFactory(): SolverFactory

    @JsName("build")
    fun build(): Solver

    @JsName("buildMutable")
    fun buildMutable(): MutableSolver

    @JsName("unificator")
    var unificator: Unificator

    @JsName("setUnificator")
    fun unificator(unificator: Unificator): SolverBuilder

    @JsName("runtime")
    var runtime: Runtime

    @JsName("setRuntime")
    fun runtime(runtime: Runtime): SolverBuilder

    @JsName("builtins")
    var builtins: Library?

    @JsName("setBuiltins")
    fun builtins(builtins: Library): SolverBuilder

    @JsName("noBuiltins")
    fun noBuiltins(): SolverBuilder

    @JsName("flags")
    var flags: FlagStore

    @JsName("setFlags")
    fun flags(flags: FlagStore): SolverBuilder

    @JsName("setFlag")
    fun flag(name: String, value: Term): SolverBuilder

    @JsName("setFlagByPair")
    fun flag(flag: Pair<String, Term>): SolverBuilder

    @JsName("setNotableFlag")
    fun flag(flag: NotableFlag): SolverBuilder

    @JsName("setNotableFlagValue")
    fun flag(flag: NotableFlag, value: Term): SolverBuilder

    @JsName("setNotableFlagByValue")
    fun <T : NotableFlag> flag(flag: T, value: T.() -> Term): SolverBuilder

    @JsName("staticKb")
    var staticKb: Theory

    @JsName("setStaticKb")
    fun staticKb(theory: Theory): SolverBuilder

    @JsName("setStaticKbByClauses")
    fun staticKb(vararg clauses: Clause): SolverBuilder

    @JsName("setStaticKbByClausesIterable")
    fun staticKb(clauses: Iterable<Clause>): SolverBuilder

    @JsName("setStaticKbByClausesSequence")
    fun staticKb(clauses: Sequence<Clause>): SolverBuilder

    @JsName("dynamicKb")
    var dynamicKb: Theory

    @JsName("setDynamicKb")
    fun dynamicKb(theory: Theory): SolverBuilder

    @JsName("setDynamicKbByClauses")
    fun dynamicKb(vararg clauses: Clause): SolverBuilder

    @JsName("setDynamicKbByClausesIterable")
    fun dynamicKb(clauses: Iterable<Clause>): SolverBuilder

    @JsName("setDynamicKbByClausesSequence")
    fun dynamicKb(clauses: Sequence<Clause>): SolverBuilder

    @JsName("inputStore")
    var inputs: InputStore

    @JsName("setInputs")
    fun inputs(inputs: InputStore): SolverBuilder

    @JsName("setInput")
    fun input(alias: String, channel: InputChannel<String>): SolverBuilder

    @JsName("standardInput")
    var standardInput: InputChannel<String>

    @JsName("setStandardInput")
    fun standardInput(channel: InputChannel<String>): SolverBuilder

    @JsName("outputStore")
    var outputs: OutputStore

    @JsName("setOutputs")
    fun outputs(outputs: OutputStore): SolverBuilder

    @JsName("setOutput")
    fun output(alias: String, channel: OutputChannel<String>): SolverBuilder

    @JsName("standardOutput")
    var standardOutput: OutputChannel<String>

    @JsName("setStandardOutput")
    fun standardOutput(channel: OutputChannel<String>): SolverBuilder

    @JsName("standardError")
    var standardError: OutputChannel<String>

    @JsName("setStandardError")
    fun standardError(channel: OutputChannel<String>): SolverBuilder

    @JsName("warnings")
    var warnings: OutputChannel<Warning>

    @JsName("setWarnings")
    fun warnings(channel: OutputChannel<Warning>): SolverBuilder
}
