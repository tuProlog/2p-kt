package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.OutputStoreImpl
import it.unibo.tuprolog.solve.exception.Warning
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A [ChannelStore] of [OutputChannel]s of [String]s (plus a dedicated [warnings] channel), as held by a
 * [it.unibo.tuprolog.solve.Solver] (see [it.unibo.tuprolog.solve.ExecutionContextAware.outputChannels]).
 */
interface OutputStore : ChannelStore<String, OutputChannel<String>, OutputStore> {
    /** The channel aliased [STDOUT], i.e. this store's standard output. */
    @JsName("stdOut")
    val stdOut: OutputChannel<String>

    /** Returns a copy of this store with [stdOut] (and the [STDOUT] alias) replaced by [channel]. */
    @JsName("setStdOut")
    fun setStdOut(channel: OutputChannel<String>): OutputStore

    /** The channel aliased [STDERR], i.e. this store's standard error. */
    @JsName("stdErr")
    val stdErr: OutputChannel<String>

    /** Returns a copy of this store with [stdErr] (and the [STDERR] alias) replaced by [channel]. */
    @JsName("setStdErr")
    fun setStdErr(channel: OutputChannel<String>): OutputStore

    /** The channel [it.unibo.tuprolog.solve.exception.Warning]s are reported to. */
    @JsName("warnings")
    val warnings: OutputChannel<Warning>

    /** Returns a copy of this store with [warnings] replaced by [channel]. */
    @JsName("setWarnings")
    fun setWarnings(channel: OutputChannel<Warning>): OutputStore

    companion object {
        /** The reserved alias for the standard output channel. */
        const val STDOUT = "stdout"

        /** The reserved alias for the standard error channel. */
        const val STDERR = "stderr"

        /**
         * Creates an [OutputStore] with [output] (defaulting to [OutputChannel.stdOut]) as both [stdOut] and
         * `"user_output"`, [error] (defaulting to [OutputChannel.stdErr]) as [stdErr], and [warnings] (defaulting to
         * [OutputChannel.warn]) as the warnings channel.
         */
        @JsName("fromStandard")
        @JvmStatic
        @JvmOverloads
        fun fromStandard(
            output: OutputChannel<String> = OutputChannel.stdOut(),
            error: OutputChannel<String> = OutputChannel.stdErr(),
            warnings: OutputChannel<Warning> = OutputChannel.warn(),
        ): OutputStore = OutputStoreImpl(output, error, warnings, mapOf("user_output" to output))

        /**
         * Creates an [OutputStore] out of [channels], using the [STDOUT]/[STDERR]-aliased ones (or the platform
         * defaults if absent) as [stdOut]/[stdErr], and [warnings] (defaulting to [OutputChannel.warn]) as the
         * warnings channel.
         */
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun of(
            channels: Map<String, OutputChannel<String>>,
            warnings: OutputChannel<Warning> = OutputChannel.warn(),
        ): OutputStore {
            val stdOut = channels[STDOUT] ?: OutputChannel.stdOut()
            val stdErr = channels[STDERR] ?: OutputChannel.stdErr()
            return OutputStoreImpl(stdOut, stdErr, warnings, channels)
        }
    }
}
