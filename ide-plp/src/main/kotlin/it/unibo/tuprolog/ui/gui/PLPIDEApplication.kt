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

/**
 * Standalone JavaFX launcher for a tuProlog IDE specialized for Probabilistic Logic Programming (ProbLog):
 * it wires up [it.unibo.tuprolog.ui.gui.TuPrologIDEBuilder] with a custom "Solutions" tab rendered via
 * [PLPSolutionView] (showing each solution's computed probability, and a button to open its
 * [it.unibo.tuprolog.bdd.BinaryDecisionDiagram] via [GraphRenderView] when one is available), a
 * [it.unibo.tuprolog.solve.SolveOptions] with [it.unibo.tuprolog.solve.setProbabilistic] turned on, and a
 * solver built from `Solver.problog` (see [it.unibo.tuprolog.solve.problog.ProblogSolverFactory]) enriched
 * with [OOPLib] and [IOLib]. It also triggers [GraphvizRenderer.initialize] on startup, so that BDD-to-image
 * rendering is ready (or known to be unavailable) by the time the user opens a diagram.
 *
 * Embed ProbLog support into a bigger application by reproducing this wiring around
 * [it.unibo.tuprolog.ui.gui.TuPrologIDEBuilder] directly, rather than using this class.
 */
class PLPIDEApplication : Application() {
    companion object {
        /** Launches the tuProlog IDE for Probabilistic Logic Programming as a standalone application. */
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PLPIDEApplication::class.java)
        }
    }

    /**
     * Initializes [GraphvizRenderer], then builds and shows a [it.unibo.tuprolog.ui.gui.TuPrologIDEBuilder]
     * configured with the custom "Solutions" tab and the ProbLog solver, on [stage].
     *
     * @throws Error wrapping any [Throwable] raised while initializing the renderer or building/showing the
     * IDE (after printing its stack trace).
     */
    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown", "PrintStackTrace")
    override fun start(stage: Stage) {
        val solutionsListView = ListView<Solution>()
        val customSolutionsTab = Tab("Solutions", solutionsListView)
        customSolutionsTab.id = "tabSolutions" // This substitutes the existing solution tab
        @Suppress("NoNameShadowing")
        solutionsListView.setCellFactory { ListCellView { PLPSolutionView.of(it) } }

        try {
            GraphvizRenderer.initialize()
            TuPrologIDEBuilder(
                stage,
                title = "tuProlog IDE for Probabilistic Logic Programming",
                customTabs =
                    listOf(
                        CustomTab(
                            customSolutionsTab,
                        ) { this.configureModel(it, solutionsListView, customSolutionsTab) },
                    ),
            ).show()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw Error(e)
        }
    }

    /** Terminates the JVM (with exit code `0`) once the JavaFX application stops. */
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
                warnings = it.warnings,
            )
        }
    }

    private fun Tab.showNotification() {
        if (!isSelected && !text.endsWith("*")) {
            text += "*"
        }
    }
}
