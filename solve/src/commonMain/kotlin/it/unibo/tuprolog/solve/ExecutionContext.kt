package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.data.CustomDataStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * An interface representing the Solver execution context, containing important information that determines its
 * behaviour.
 *
 * An [ExecutionContext] is a [Solver]'s entire mutable state, reified as an immutable value: the current
 * [substitution], the [logicStackTrace], [customData], and (via [ExecutionContextAware]) the unificator, libraries,
 * flags, both knowledge bases, and I/O channels. It is what
 * [it.unibo.tuprolog.solve.primitive.Solve.Request]s/[it.unibo.tuprolog.solve.primitive.Solve.Response]s carry
 * around, letting [it.unibo.tuprolog.solve.primitive.Primitive]s and
 * [it.unibo.tuprolog.solve.function.LogicFunction]s observe (and, via [apply]/[update], derive new versions of) the
 * state of the resolution they're running within, without depending on any concrete resolution strategy.
 *
 * Because each of these pieces of state is itself an immutable data structure, "mutating" an [ExecutionContext]
 * always means producing a new instance (e.g. [update] or [apply]) rather than changing this one in place -- which
 * keeps every intermediate state snapshot-able, a property resolution strategies rely on for backtracking.
 *
 * Resolution strategies (e.g. `:solve-classic`'s state-machine solver) are free to extend this interface with
 * whatever extra bookkeeping they personally need; code written against the generic [ExecutionContext] keeps
 * working regardless of which concrete strategy produced the instance.
 *
 * @see Solver
 * @see ExecutionContextAware
 */
interface ExecutionContext :
    ExecutionContextAware,
    Durable {
    /** The current procedure being executed, or `null` if none is (e.g. at the very start of resolution) */
    @JsName("procedure")
    val procedure: Struct?

    /** The set of current substitution till this context */
    @JsName("substitution")
    val substitution: Substitution.Unifier

    /** The Prolog call stacktrace till this ExecutionContext */
    @JsName("logicStackTrace")
    val logicStackTrace: List<Struct>

    /** Custom, implementation- or library-defined data attached to this context, keyed by [it.unibo.tuprolog.solve.data.CustomData] type. */
    @JsName("customData")
    val customData: CustomDataStore

    /**
     * Creates a new, independent [Solver] sharing this context's state (unless overridden by the arguments), to be
     * used e.g. by primitives that need to recursively solve a sub-goal (see
     * [it.unibo.tuprolog.solve.primitive.Solve.Request.subSolver]).
     */
    @JsName("createSolver")
    fun createSolver(
        unificator: Unificator = this.unificator,
        libraries: Runtime = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels,
    ): Solver

    /** Same as [createSolver], but returns a [MutableSolver]. */
    @JsName("createMutableSolver")
    fun createMutableSolver(
        unificator: Unificator = this.unificator,
        libraries: Runtime = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels,
    ): MutableSolver

    /** Returns a new [ExecutionContext], obtained by applying [sideEffect] to this one, via [SideEffect.applyTo]. */
    @JsName("apply")
    fun apply(sideEffect: SideEffect): ExecutionContext = apply(listOf(sideEffect))

    /**
     * Returns a new [ExecutionContext], obtained by applying every [sideEffects] item, in order, to this one (each
     * one to the result of the previous application).
     */
    @JsName("applyIterable")
    fun apply(sideEffects: Iterable<SideEffect>): ExecutionContext {
        var current = this
        for (sideEffect in sideEffects) {
            current = sideEffect.applyTo(current)
        }
        return current
    }

    /** Same as [apply] for an [Iterable], but accepting a [Sequence] of [sideEffects]. */
    @JsName("applySequence")
    fun apply(sideEffects: Sequence<SideEffect>): ExecutionContext = apply(sideEffects.asIterable())

    /**
     * Returns a new [ExecutionContext], identical to this one except for every explicitly-provided argument, which
     * replaces the corresponding piece of state. Arguments left unspecified default to this context's current
     * value.
     */
    @JsName("update")
    fun update(
        unificator: Unificator = this.unificator,
        libraries: Runtime = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        operators: OperatorSet = this.operators,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels,
        customData: CustomDataStore = this.customData,
    ): ExecutionContext
}
