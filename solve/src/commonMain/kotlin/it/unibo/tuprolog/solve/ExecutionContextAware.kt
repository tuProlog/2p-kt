package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** Base type for all entities which must be aware of the current state of a solver */
interface ExecutionContextAware {

    companion object {
        const val STDIN = "\$stdin"
        const val STDOUT = "\$stdout"
        const val STDERR = "\$stderr"
        const val WARNINGS = "\$warnings"

        @JvmStatic
        @JsName("defaultInputChannels")
        fun defaultInputChannels(): PrologInputChannels<*> {
            return mapOf(STDIN to InputChannel.stdIn())
        }

        @JvmStatic
        @JsName("defaultOutputChannels")
        fun defaultOutputChannels(): PrologOutputChannels<*> {
            return mapOf(
                STDOUT to OutputChannel.stdOut<String>(),
                STDERR to OutputChannel.stdErr<String>(),
                WARNINGS to OutputChannel.stdErr<PrologWarning>()
            )
        }

        private inline fun <reified T> Any?.castOrNull(): T? {
            @Suppress("UNCHECKED_CAST")
            return if (this !== null) this as T else null
        }
    }

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
    val inputChannels: PrologInputChannels<*>

    /** The currently open output channels */
    @JsName("outputChannels")
    val outputChannels: PrologOutputChannels<*>

    /** Shortcut for the standard input channel defined in [inputChannels], and named as [STDIN].
     * Returns `null` if the channel is closed
     */
    @JsName("standardInput")
    val standardInput: InputChannel<String>?
        get() {
            return inputChannels[STDIN].castOrNull()
        }

    /**
     * Shortcut for the standard output channel defined in [outputChannels], and named as [STDOUT].
     * Returns `null` if the channel is closed
     */
    @JsName("standardOutput")
    val standardOutput: OutputChannel<String>?
        get() {
            return outputChannels[STDOUT].castOrNull()
        }

    /** Shortcut for the standard error channel defined in [outputChannels], and named as [STDERR].
     * Returns `null` if the channel is closed
     */
    @JsName("standardError")
    val standardError: OutputChannel<String>?
        get() {
            return outputChannels[STDERR].castOrNull()
        }

    /** Shortcut for the warnings channel defined in [outputChannels], and named as [WARNINGS].
     * Returns `null` if the channel is closed
     */
    @JsName("warnings")
    val warnings: OutputChannel<PrologWarning>?
        get() {
            return outputChannels[WARNINGS].castOrNull()
        }
}