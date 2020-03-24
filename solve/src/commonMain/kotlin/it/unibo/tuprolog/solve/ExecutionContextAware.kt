package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

/** Base type for all entities which must be aware of the current state of a solver */
interface ExecutionContextAware {

    companion object {
        const val STDIN = "\$stdin"
        const val STDOUT = "\$stdout"
        const val STDERR = "\$stderr"
        const val WARNINGS = "\$warnings"

        fun defaultInputChannels(): PrologInputChannels<*> {
            return mapOf(STDIN to InputChannel.stdIn())
        }

        fun defaultOutputChannels(): PrologOutputChannels<*> {
            return mapOf(
                STDOUT to OutputChannel.stdOut<String>(),
                STDERR to OutputChannel.stdErr<String>(),
                WARNINGS to OutputChannel.stdErr<PrologWarning>()
            )
        }
    }

    /** Loaded libraries */
    val libraries: Libraries

    /** Enabled flags */
    val flags: PrologFlags

    /** Static Knowledge-base, that is a KB that *can't* change executing goals */
    val staticKB: ClauseDatabase

    /** Dynamic Knowledge-base, that is a KB that *can* change executing goals */
    val dynamicKB: ClauseDatabase

    /** The currently open input channels */
    val inputChannels: PrologInputChannels<*>

    /** The currently open output channels */
    val outputChannels: PrologOutputChannels<*>

    /** Shortcut for the standard input channel defined in [inputChannels], and named as [STDIN].
     * Returns `null` if the channel is closed
     */
    val standardInput: InputChannel<String>?
        get() {
            return inputChannels[STDIN] as InputChannel<String>
        }

    /**
     * Shortcut for the standard output channel defined in [outputChannels], and named as [STDOUT].
     * Returns `null` if the channel is closed
     */
    val standardOutput: OutputChannel<String>?
        get() {
            return outputChannels[STDOUT] as OutputChannel<String>
        }

    /** Shortcut for the standard error channel defined in [outputChannels], and named as [STDERR].
     * Returns `null` if the channel is closed
     */
    val standardError: OutputChannel<String>?
        get() {
            return outputChannels[STDERR] as OutputChannel<String>
        }

    /** Shortcut for the warnings channel defined in [outputChannels], and named as [WARNINGS].
     * Returns `null` if the channel is closed
     */
    val warnings: OutputChannel<PrologWarning>?
        get() {
            return outputChannels[WARNINGS] as OutputChannel<PrologWarning>
        }
}