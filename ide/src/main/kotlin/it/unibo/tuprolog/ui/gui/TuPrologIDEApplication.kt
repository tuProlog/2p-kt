package it.unibo.tuprolog.ui.gui

import javafx.application.Application
import javafx.stage.Stage
import kotlin.system.exitProcess

/**
 * Standalone JavaFX launcher for the tuProlog IDE: it shows the default [TuPrologIDEBuilder] configuration
 * on the primary [Stage] and terminates the JVM when the window closes. Embed the IDE into a bigger
 * application by using [TuPrologIDEBuilder] directly instead of this class.
 */
class TuPrologIDEApplication : Application() {
    /**
     * Builds and shows the default [TuPrologIDEBuilder] on [stage].
     *
     * @throws Error wrapping any [Throwable] raised while building/showing the IDE (after printing its stack trace).
     */
    @Suppress("TooGenericExceptionCaught", "PrintStackTrace", "TooGenericExceptionThrown")
    override fun start(stage: Stage) {
        try {
            TuPrologIDEBuilder(stage).show()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw Error(e)
        }
    }

    /** Terminates the JVM (with exit code `0`) once the JavaFX application stops. */
    override fun stop() {
        exitProcess(0)
    }

    companion object {
        /** Launches the tuProlog IDE as a standalone application. */
        @JvmStatic
        fun main(args: Array<String>) {
            launch(TuPrologIDEApplication::class.java)
        }
    }
}
