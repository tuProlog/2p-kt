package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.libs.io.IOLib
import it.unibo.tuprolog.solve.libs.oop.OOPLib
import it.unibo.tuprolog.solve.setProbabilistic
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.stage.Stage
import kotlin.system.exitProcess

class PLPIDEApplication : Application() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PLPIDEApplication::class.java)
        }
    }

    override fun start(stage: Stage) {
        val solutionsListView = ListView<Solution>()
        val customSolutionsTab = Tab("Solutions", solutionsListView)
        customSolutionsTab.id = "tabSolutions" // This substitutes the existing solution tab
        solutionsListView.setCellFactory { ListCellView { PLPSolutionView.of(it) } }

        try {
            GraphvizRenderer.initialize()
            TuPrologIDEBuilder(
                stage,
                title = "tuProlog IDE for Probabilistic Logic Programming",
                customTabs = listOf(
                    CustomTab(
                        customSolutionsTab,
                    ) { this.configureModel(it, solutionsListView, customSolutionsTab) }
                )
            ).show()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw Error(e)
        }
    }

    override fun stop() {
        exitProcess(0)
    }

    private fun configureModel(
        model: TuPrologIDEModel,
        listView: ListView<Solution>,
        listTab: Tab,
    ) {
        // Hook events to the custom solutions tab
        model.onReset.subscribe { listView.items.clear() }
        model.onNewQuery.subscribe { listView.items.clear() }
        model.onNewSolution.subscribe {
            Platform.runLater {
                listView.items.add(it.event)
                listView.scrollTo(it.event)
                listTab.showNotification()
            }
        }

        // Set the SolveOptions.probabilistic flag to true
        model.solveOptions = model.solveOptions.setProbabilistic(true)

        // Create a solver with PLP support
        model.customizeSolver {
            Solver.problog.mutableSolverWithDefaultBuiltins(
                otherLibraries = Runtime.of(OOPLib, IOLib),
                stdIn = it.standardInput,
                stdOut = it.standardOutput,
                stdErr = it.standardError,
                warnings = it.warnings
            )
        }
    }

    private fun Tab.showNotification() {
        if (!isSelected && !text.endsWith("*")) {
            text += "*"
        }
    }
}
