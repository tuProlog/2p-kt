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

/**
 * A mutable, fluent builder for [Solver]/[MutableSolver] instances, as an alternative to calling
 * [SolverFactory.solverOf] (and friends) with many named arguments at once.
 *
 * Every piece of state exposed here is both a settable `var` property and a same-named `fun` that sets it and
 * returns `this`, so calls can be chained, e.g.:
 * ```kotlin
 * val solver =
 *     Solver.prolog.newBuilder()
 *         .staticKb(myTheory)
 *         .standardOutput(OutputChannel.of { print(it) })
 *         .build()
 * ```
 *
 * Obtain one via [SolverFactory.newBuilder]; every property starts out at the originating factory's `default*`
 * value.
 *
 * @see SolverFactory
 */
interface SolverBuilder {
    /** Freezes the current configuration of this builder into a [SolverFactory] whose defaults match it. */
    @JsName("toFactory")
    fun toFactory(): SolverFactory

    /** Builds a [Solver] out of the current configuration of this builder. */
    @JsName("build")
    fun build(): Solver

    /** Builds a [MutableSolver] out of the current configuration of this builder. */
    @JsName("buildMutable")
    fun buildMutable(): MutableSolver

    /** The [Unificator] the built solver will use. */
    @JsName("unificator")
    var unificator: Unificator

    /** Fluent setter for [unificator]. */
    @JsName("setUnificator")
    fun unificator(unificator: Unificator): SolverBuilder

    /** The [Runtime] of libraries the built solver will load (in addition to [builtins], if any). */
    @JsName("runtime")
    var runtime: Runtime

    /** Fluent setter for [runtime]. */
    @JsName("setRuntime")
    fun runtime(runtime: Runtime): SolverBuilder

    /**
     * Adds an anonymous (or [alias]ed) library, assembled on the fly out of [item1] and [items]
     * (via [it.unibo.tuprolog.solve.libraryOf]), to [runtime].
     */
    @JsName("setLibrary")
    fun library(
        alias: String?,
        item1: AbstractWrapper<*>,
        vararg items: AbstractWrapper<*>,
    ): SolverBuilder

    /** Same as [library], generating an anonymous alias automatically. */
    @JsName("setLibraryWithDefaultAlias")
    fun library(
        item1: AbstractWrapper<*>,
        vararg items: AbstractWrapper<*>,
    ): SolverBuilder

    /** The standard-library [Library] the built solver will load, or `null` if none (see [noBuiltins]). */
    @JsName("builtins")
    var builtins: Library?

    /** Fluent setter for [builtins]. */
    @JsName("setBuiltins")
    fun builtins(builtins: Library): SolverBuilder

    /** Sets [builtins] to `null`, so the built solver won't load any standard-library predicate. */
    @JsName("noBuiltins")
    fun noBuiltins(): SolverBuilder

    /** The [FlagStore] the built solver will start with. */
    @JsName("flags")
    var flags: FlagStore

    /** Fluent setter for [flags]. */
    @JsName("setFlags")
    fun flags(flags: FlagStore): SolverBuilder

    /** Sets the flag named [name] within [flags] to [value]. */
    @JsName("setFlag")
    fun flag(
        name: String,
        value: Term,
    ): SolverBuilder

    /** Sets the flag identified by [flag]'s first component within [flags] to its second component. */
    @JsName("setFlagByPair")
    fun flag(flag: Pair<String, Term>): SolverBuilder

    /** Sets [flag] within [flags] to its default term value ([NotableFlag.defaultTerm]). */
    @JsName("setNotableFlag")
    fun flag(flag: NotableFlag): SolverBuilder

    /** Sets [flag] within [flags] to [value]. */
    @JsName("setNotableFlagValue")
    fun flag(
        flag: NotableFlag,
        value: Term,
    ): SolverBuilder

    /** Sets [flag] within [flags] to the [Term] computed by [value], applied to [flag] itself. */
    @JsName("setNotableFlagByValue")
    fun <T : NotableFlag> flag(
        flag: T,
        value: T.() -> Term,
    ): SolverBuilder

    /** The static knowledge base the built solver will start with. */
    @JsName("staticKb")
    var staticKb: Theory

    /** Fluent setter for [staticKb]. */
    @JsName("setStaticKb")
    fun staticKb(theory: Theory): SolverBuilder

    /** Sets [staticKb] to a [Theory] built out of [clauses], using the current [unificator]. */
    @JsName("setStaticKbByClauses")
    fun staticKb(vararg clauses: Clause): SolverBuilder

    /** Sets [staticKb] to a [Theory] built out of [clauses], using the current [unificator]. */
    @JsName("setStaticKbByClausesIterable")
    fun staticKb(clauses: Iterable<Clause>): SolverBuilder

    /** Sets [staticKb] to a [Theory] built out of [clauses], using the current [unificator]. */
    @JsName("setStaticKbByClausesSequence")
    fun staticKb(clauses: Sequence<Clause>): SolverBuilder

    /** The dynamic knowledge base the built solver will start with. */
    @JsName("dynamicKb")
    var dynamicKb: Theory

    /** Fluent setter for [dynamicKb]. */
    @JsName("setDynamicKb")
    fun dynamicKb(theory: Theory): SolverBuilder

    /** Sets [dynamicKb] to a [Theory] built out of [clauses], using the current [unificator]. */
    @JsName("setDynamicKbByClauses")
    fun dynamicKb(vararg clauses: Clause): SolverBuilder

    /** Sets [dynamicKb] to a [Theory] built out of [clauses], using the current [unificator]. */
    @JsName("setDynamicKbByClausesIterable")
    fun dynamicKb(clauses: Iterable<Clause>): SolverBuilder

    /** Sets [dynamicKb] to a [Theory] built out of [clauses], using the current [unificator]. */
    @JsName("setDynamicKbByClausesSequence")
    fun dynamicKb(clauses: Sequence<Clause>): SolverBuilder

    /** The [it.unibo.tuprolog.solve.channel.InputStore] the built solver will start with. */
    @JsName("inputStore")
    var inputs: InputStore

    /** Fluent setter for [inputs]. */
    @JsName("setInputs")
    fun inputs(inputs: InputStore): SolverBuilder

    /** Adds [channel] to [inputs] under [alias]. */
    @JsName("setInput")
    fun input(
        alias: String,
        channel: InputChannel<String>,
    ): SolverBuilder

    /** The standard input channel within [inputs]. */
    @JsName("standardInput")
    var standardInput: InputChannel<String>

    /** Fluent setter for [standardInput]. */
    @JsName("setStandardInput")
    fun standardInput(channel: InputChannel<String>): SolverBuilder

    /** The [it.unibo.tuprolog.solve.channel.OutputStore] the built solver will start with. */
    @JsName("outputStore")
    var outputs: OutputStore

    /** Fluent setter for [outputs]. */
    @JsName("setOutputs")
    fun outputs(outputs: OutputStore): SolverBuilder

    /** Adds [channel] to [outputs] under [alias]. */
    @JsName("setOutput")
    fun output(
        alias: String,
        channel: OutputChannel<String>,
    ): SolverBuilder

    /** The standard output channel within [outputs]. */
    @JsName("standardOutput")
    var standardOutput: OutputChannel<String>

    /** Fluent setter for [standardOutput]. */
    @JsName("setStandardOutput")
    fun standardOutput(channel: OutputChannel<String>): SolverBuilder

    /** The standard error channel within [outputs]. */
    @JsName("standardError")
    var standardError: OutputChannel<String>

    /** Fluent setter for [standardError]. */
    @JsName("setStandardError")
    fun standardError(channel: OutputChannel<String>): SolverBuilder

    /** The warnings channel within [outputs]. */
    @JsName("warnings")
    var warnings: OutputChannel<Warning>

    /** Fluent setter for [warnings]. */
    @JsName("setWarnings")
    fun warnings(channel: OutputChannel<Warning>): SolverBuilder
}
