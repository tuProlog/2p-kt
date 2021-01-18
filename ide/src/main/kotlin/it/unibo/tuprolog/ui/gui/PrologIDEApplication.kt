package it.unibo.tuprolog.ui.gui

import javafx.application.Application
import javafx.stage.Stage
import kotlin.system.exitProcess

class PrologIDEApplication : Application() {
    override fun start(stage: Stage) {
        try {
            PrologIdeBuilder(stage).show()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw Error(e)
        }
    }

    override fun stop() {
        exitProcess(0)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PrologIDEApplication::class.java)
        }
    }
}
