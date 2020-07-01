package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
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

    @JsName("createSolver")
    fun createSolver(
        libraries: Libraries = this.libraries,
        flags: PrologFlags = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        stdIn: InputChannel<String> = this.standardInput ?: InputChannel.stdIn(),
        stdOut: OutputChannel<String> = this.standardOutput ?: OutputChannel.stdOut(),
        stdErr: OutputChannel<String> = this.standardError ?: OutputChannel.stdErr(),
        warnings: OutputChannel<PrologWarning> = this.warnings ?: OutputChannel.stdErr()
    ): Solver

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
                    flags = flags + sideEffect.flags
                }
                is SideEffect.ResetFlags -> {
                    flags = sideEffect.flags
                }
                is SideEffect.ClearFlags -> {
                    flags = flags - sideEffect.names
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
                    inputChannels = inputChannels + sideEffect.inputChannels
                }
                is SideEffect.ResetInputChannels -> {
                    inputChannels = sideEffect.inputChannels
                }
                is SideEffect.CloseInputChannels -> {
                    inputChannels = inputChannels - sideEffect.names
                }
                is SideEffect.OpenOutputChannels -> {
                    outputChannels = outputChannels + sideEffect.outputChannels
                }
                is SideEffect.ResetOutputChannels -> {
                    outputChannels = sideEffect.outputChannels
                }
                is SideEffect.CloseOutputChannels -> {
                    outputChannels = outputChannels - sideEffect.names
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
            outputChannels = outputChannels
        )
    }

    @JsName("applySequence")
    fun apply(sideEffects: Sequence<SideEffect>): ExecutionContext =
        apply(sideEffects.asIterable())

    @JsName("update")
    fun update(
        libraries: Libraries = this.libraries,
        flags: PrologFlags = this.flags,
        staticKb: Theory = this.staticKb,
        dynamicKb: Theory = this.dynamicKb,
        operators: OperatorSet = this.operators,
        inputChannels: PrologInputChannels<*> = this.inputChannels,
        outputChannels: PrologOutputChannels<*> = this.outputChannels
    ): ExecutionContext
}