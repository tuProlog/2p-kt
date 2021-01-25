package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputStore
import it.unibo.tuprolog.solve.channel.OutputStore
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

/** An interface representing the Solver execution context, containing important information that determines its behaviour */
// TODO: 25/09/2019 solverStrategies should go here... in common with other implementations, if the idea is approved
interface ExecutionContext : ExecutionContextAware {

    /** The current procedure being executed */
    @JsName("procedure")
    val procedure: Struct?

    /** The set of current substitution till this context */
    @JsName("substitution")
    val substitution: Substitution.Unifier

    /** The Prolog call stacktrace till this ExecutionContext */
    @JsName("prologStackTrace")
    val prologStackTrace: List<Struct>

    @JsName("durableData")
    val durableData: Map<String, Any>

    @JsName("ephemeralData")
    val ephemeralData: Map<String, Any>

    @JsName("createSolver")
    fun createSolver(
        libraries: Libraries = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels
    ): Solver

    @JsName("createMutableSolver")
    fun createMutableSolver(
        libraries: Libraries = this.libraries,
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
        var dynamicKb = dynamicKb
        var staticKb = staticKb
        var flags = flags
        var libraries = libraries
        var operators = operators
        var inputChannels = inputChannels
        var outputChannels = outputChannels
        var durableData = durableData
        var ephemeralData = ephemeralData

        for (sideEffect in sideEffects) {
            when (sideEffect) {
                is SideEffect.AddStaticClauses -> {
                    staticKb = if (sideEffect.onTop) {
                        staticKb.assertA(sideEffect.clauses)
                    } else {
                        staticKb.assertZ(sideEffect.clauses)
                    }
                }
                is SideEffect.AddDynamicClauses -> {
                    dynamicKb = if (sideEffect.onTop) {
                        dynamicKb.assertA(sideEffect.clauses)
                    } else {
                        dynamicKb.assertZ(sideEffect.clauses)
                    }
                }
                is SideEffect.ResetStaticKb -> {
                    staticKb = sideEffect.theory
                }
                is SideEffect.ResetDynamicKb -> {
                    dynamicKb = sideEffect.theory
                }
                is SideEffect.RemoveStaticClauses -> {
                    staticKb = staticKb.retract(sideEffect.clauses).theory
                }
                is SideEffect.RemoveDynamicClauses -> {
                    dynamicKb = dynamicKb.retract(sideEffect.clauses).theory
                }
                is SideEffect.SetFlags -> {
                    flags += sideEffect.flags
                }
                is SideEffect.ResetFlags -> {
                    flags = FlagStore.of(sideEffect.flags)
                }
                is SideEffect.ClearFlags -> {
                    flags -= sideEffect.names
                }
                is SideEffect.LoadLibrary -> {
                    libraries += sideEffect.aliasedLibrary
                }
                is SideEffect.UpdateLibrary -> {
                    libraries = libraries.update(sideEffect.aliasedLibrary)
                }
                is SideEffect.UnloadLibraries -> {
                    libraries -= sideEffect.aliases
                }
                is SideEffect.AddLibraries -> {
                    libraries += sideEffect.libraries
                }
                is SideEffect.ResetLibraries -> {
                    libraries = sideEffect.libraries
                }
                is SideEffect.SetOperators -> {
                    operators += sideEffect.operatorSet
                }
                is SideEffect.ResetOperators -> {
                    operators = sideEffect.operatorSet
                }
                is SideEffect.RemoveOperators -> {
                    operators -= sideEffect.operatorSet
                }
                is SideEffect.OpenInputChannels -> {
                    inputChannels += sideEffect.inputChannels
                }
                is SideEffect.ResetInputChannels -> {
                    inputChannels = InputStore.of(sideEffect.inputChannels)
                }
                is SideEffect.CloseInputChannels -> {
                    inputChannels -= sideEffect.names
                }
                is SideEffect.OpenOutputChannels -> {
                    outputChannels += sideEffect.outputChannels
                }
                is SideEffect.ResetOutputChannels -> {
                    outputChannels = OutputStore.of(sideEffect.outputChannels, warnings)
                }
                is SideEffect.CloseOutputChannels -> {
                    outputChannels -= sideEffect.names
                }
                is SideEffect.SetDurableData -> {
                    durableData = if (sideEffect.reset) {
                        sideEffect.data
                    } else {
                        durableData + sideEffect.data
                    }
                }
                is SideEffect.SetEphemeralData -> {
                    ephemeralData = if (sideEffect.reset) {
                        sideEffect.data
                    } else {
                        ephemeralData + sideEffect.data
                    }
                }
            }
        }
        return update(
            dynamicKb = dynamicKb,
            staticKb = staticKb,
            flags = flags,
            libraries = libraries,
            operators = operators,
            inputChannels = inputChannels,
            outputChannels = outputChannels,
            durableData = durableData,
            ephemeralData = ephemeralData
        )
    }

    @JsName("applySequence")
    fun apply(sideEffects: Sequence<SideEffect>): ExecutionContext = apply(sideEffects.asIterable())

    @JsName("update")
    fun update(
        libraries: Libraries = this.libraries,
        flags: FlagStore = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        operators: OperatorSet = this.operators,
        inputChannels: InputStore = this.inputChannels,
        outputChannels: OutputStore = this.outputChannels,
        durableData: Map<String, Any> = this.durableData,
        ephemeralData: Map<String, Any> = this.ephemeralData
    ): ExecutionContext
}
