package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.Solver
import javafx.application.Application
import javafx.stage.Stage
import kotlin.system.exitProcess

class TuPrologIDE : Application() {
    override fun start(stage: Stage) {
        try {
            TuPrologIDEBuilder(stage, solverFactory = Solver.prolog).show()
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
            launch(TuPrologIDE::class.java)
        }
    }
}
