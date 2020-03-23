package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase

/** Base type for all entities which must be aware of the current state of a solver */
interface ExecutionContextAware {

    companion object {
        const val STDIN = "\$stdin"
        const val STDOUT = "\$stdout"
        const val STDERR = "\$stderr"

        fun defaultInputChannels(): PrologInputChannels<String> {
            return mapOf(STDIN to InputChannel.stdin)
        }

        fun defaultOutputChannels(): PrologOutputChannels<String> {
            return mapOf(
                STDOUT to OutputChannel.stdout,
                STDERR to OutputChannel.stderr
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
    val inputChannels: PrologInputChannels<String>

    /** The currently open output channels */
    val outputChannels: PrologOutputChannels<String>

    /** Shortcut for the standard input channel defined in [inputChannels].
     * Returns `null` if the channel is closed
     */
    val stdin: InputChannel<String>?
        get() = inputChannels[STDIN]

    /**
     * Shortcut for the standard output channel defined in [outputChannels].
     * Returns `null` if the channel is closed
     */
    val stdout: OutputChannel<String>?
        get() = outputChannels[STDOUT]

    /** Shortcut for the standard error channel defined in [outputChannels].
     * Returns `null` if the channel is closed
     */
    val stderr: OutputChannel<String>?
        get() = outputChannels[STDERR]

}