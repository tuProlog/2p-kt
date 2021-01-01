package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

/** Base type for all entities which must be aware of the current state of a solver */
interface ExecutionContextAware {

    /** Loaded libraries */
    @JsName("libraries")
    val libraries: Libraries

    /** Enabled flags */
    @JsName("flags")
    val flags: FlagStore

    /** Static Knowledge-base, that is a KB that *can't* change executing goals */
    @JsName("staticKb")
    val staticKb: Theory

    /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
    @JsName("dynamicKb")
    val dynamicKb: Theory

    /** Loaded operators */
    @JsName("operators")
    val operators: OperatorSet

    /** The currently open input channels */
    @JsName("inputChannels")
    val inputChannels: InputStore

    /** The currently open output channels */
    @JsName("outputChannels")
    val outputChannels: OutputStore

    /** Shortcut for the standard input channel defined in [inputChannels].
     * Returns `null` if the channel is closed
     */
    @JsName("standardInput")
    val standardInput: InputChannel<String>?
        get() = inputChannels.stdIn

    /**
     * Shortcut for the standard output channel defined in [outputChannels].
     * Returns `null` if the channel is closed
     */
    @JsName("standardOutput")
    val standardOutput: OutputChannel<String>?
        get() = outputChannels.stdOut

    /** Shortcut for the standard error channel defined in [outputChannels].
     * Returns `null` if the channel is closed
     */
    @JsName("standardError")
    val standardError: OutputChannel<String>?
        get() = outputChannels.stdErr

    /** Shortcut for the warnings channel defined in [outputChannels].
     * Returns `null` if the channel is closed
     */
    @JsName("warnings")
    val warnings: OutputChannel<PrologWarning>
        get() = outputChannels.warnings
}
