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

/** An interface representing the Solver execution context, containing important information that determines its behaviour */
// TODO: 25/09/2019 solverStrategies should go here... in common with other implementations, if the idea is approved
interface ExecutionContext : ExecutionContextAware, Durable {

    /** The current procedure being executed */
    @JsName("procedure")
    val procedure: Struct?

    /** The set of current substitution till this context */
    @JsName("substitution")
    val substitution: Substitution.Unifier

    /** The Prolog call stacktrace till this ExecutionContext */
    @JsName("logicStackTrace")
    val logicStackTrace: List<Struct>

    @JsName("customData")
    val customData: CustomDataStore

    @JsName("createSolver")
    fun createSolver(
        unificator: Unificator = this.unificator,
        libraries: Runtime = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels
    ): Solver

    @JsName("createMutableSolver")
    fun createMutableSolver(
        unificator: Unificator = this.unificator,
        libraries: Runtime = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels
    ): MutableSolver

    @JsName("apply")
    fun apply(sideEffect: SideEffect): ExecutionContext {
        return apply(listOf(sideEffect))
    }

    @JsName("applyIterable")
    fun apply(sideEffects: Iterable<SideEffect>): ExecutionContext {
        var current = this
        for (sideEffect in sideEffects) {
            current = sideEffect.applyTo(current)
        }
        return current
    }

    @JsName("applySequence")
    fun apply(sideEffects: Sequence<SideEffect>): ExecutionContext = apply(sideEffects.asIterable())

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
        customData: CustomDataStore = this.customData
    ): ExecutionContext
}
