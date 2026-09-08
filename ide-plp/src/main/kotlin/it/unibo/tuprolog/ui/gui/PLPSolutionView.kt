package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.bdd.toDotString
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.binaryDecisionDiagram
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.hasBinaryDecisionDiagram
import it.unibo.tuprolog.solve.probability
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import java.io.IOException

/**
 * The ProbLog counterpart of [SolutionView]: a [VBox] rendering one [Solution] in the tuProlog IDE's custom
 * "Solutions" tab (see [PLPIDEApplication]), colored by a "led" [Circle] like [SolutionView] but additionally
 * showing, for [Solution.Yes], the solution's computed [it.unibo.tuprolog.solve.probability] and -- when a
 * [it.unibo.tuprolog.bdd.BinaryDecisionDiagram] was recorded for it (see
 * [it.unibo.tuprolog.solve.hasBinaryDecisionDiagram]) -- a button that opens it, rendered as a Graphviz graph,
 * in a [GraphRenderView] dialog. Use [of] to build the right concrete subclass for a given solution.
 */
sealed class PLPSolutionView<T, S : Solution>(
    private val solution: S,
) : VBox() {
    companion object {
        private const val FXML = "PLPSolutionView.fxml"

        private val ITEM_MARGIN = Insets(0.0, 0.0, 0.0, 55.0)

        /** Led color used for [Solution.Yes] ([YesViewPLP]); same as [SolutionView.COLOR_YES]. */
        val COLOR_YES: Paint = SolutionView.COLOR_YES

        /** Led color used for [Solution.No] ([NoViewPLP]); same as [SolutionView.COLOR_NO]. */
        val COLOR_NO: Paint = SolutionView.COLOR_NO

        /** Led color used for a [Solution.Halt] not caused by a timeout ([HaltViewPLP]);
         * same as [SolutionView.COLOR_HALT]. */
        val COLOR_HALT: Paint = SolutionView.COLOR_HALT

        /**
         * Led color used for a [Solution.Halt] caused by [it.unibo.tuprolog.solve.exception.TimeOutException]
         * ([HaltViewPLP]); same as [SolutionView.COLOR_TIMEOUT].
         */
        val COLOR_TIMEOUT: Paint = SolutionView.COLOR_TIMEOUT

        /** Builds the [PLPSolutionView] subclass ([YesViewPLP], [NoViewPLP], or [HaltViewPLP])
         * matching the kind of [solution]. */
        fun of(solution: Solution): PLPSolutionView<*, *> =
            solution.whenIs(
                yes = { YesViewPLP(it) },
                no = { NoViewPLP(it) },
                halt = { HaltViewPLP(it) },
            )
    }

    protected val formatter = TermFormatter.prettyExpressions()

    init {
        val loader = FXMLLoader(PLPSolutionView::class.java.getResource(FXML))
        loader.setController(this)
        loader.setRoot(this)

        try {
            loader.load<VBox>()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    @FXML
    lateinit var led: Circle

    @FXML
    lateinit var status: Label

    @FXML
    lateinit var query: Label

    @FXML
    lateinit var probability: Label

    @FXML
    lateinit var substitutions: VBox

    @FXML
    lateinit var btnShowBinaryDecisionDiagram: Button

    /**
     * Renders a [Solution.Yes]: the solved goal, one [AssignmentView] per variable substitution, the
     * solution's [it.unibo.tuprolog.solve.probability] (as a percentage), and -- if
     * [it.unibo.tuprolog.solve.hasBinaryDecisionDiagram] is `true` for this solution -- a visible
     * [btnShowBinaryDecisionDiagram] button wired to [onShowBinaryDecisionDiagramPressed].
     */
    @Suppress("MagicNumber")
    class YesViewPLP(
        solution: Solution.Yes,
    ) : PLPSolutionView<Pair<Var, Term>, Solution.Yes>(solution) {
        init {
            status.text = "yes:"
            led.fill = COLOR_YES
            query.text = solution.solvedQuery.format(formatter)
            solution.substitution.map { it.toPair() }.forEach {
                val assignment = AssignmentView(it, formatter)
                substitutions.children.add(assignment)
                setMargin(assignment, ITEM_MARGIN)
            }

            // Show computed probability value
            probability.text = "Probability: %.2f".format(solution.probability * 100)

            // Show button for Binary Decision Diagrams, if available
            if (solution.hasBinaryDecisionDiagram) {
                btnShowBinaryDecisionDiagram.isVisible = true
                btnShowBinaryDecisionDiagram.setOnAction { this.onShowBinaryDecisionDiagramPressed() }
            } else {
                btnShowBinaryDecisionDiagram.isVisible = false
            }
        }
    }

    /** Renders a [Solution.No]: just the "no." status, with the probability label blank and the BDD button hidden. */
    class NoViewPLP(
        solution: Solution.No,
    ) : PLPSolutionView<String, Solution.No>(solution) {
        init {
            led.fill = COLOR_NO
            status.text = "no."
            query.isVisible = false
            probability.text = ""
            btnShowBinaryDecisionDiagram.isVisible = false
        }
    }

    /**
     * Renders a [Solution.Halt]: a plain "timeout." notice if [Solution.Halt.exception] is a
     * [it.unibo.tuprolog.solve.exception.TimeOutException], otherwise the exception message followed by one
     * label per frame of [it.unibo.tuprolog.solve.exception.ResolutionException.logicStackTrace]; either way
     * the probability label is left blank and the BDD button hidden, since a halted computation carries
     * neither a probability nor a diagram.
     */
    class HaltViewPLP(
        solution: Solution.Halt,
    ) : PLPSolutionView<String, Solution.Halt>(solution) {
        init {
            probability.text = ""
            btnShowBinaryDecisionDiagram.isVisible = false
            if (solution.exception is TimeOutException) {
                led.fill = COLOR_TIMEOUT
                status.text = "timeout."
                query.isVisible = false
            } else {
                led.fill = COLOR_HALT
                status.text = "halt:"
                query.text = solution.exception.message
                with(children) {
                    solution.exception.logicStackTrace.map { it.format(formatter) }.forEach {
                        val lbl = Label("at $it")
                        add(lbl)
                        setMargin(lbl, ITEM_MARGIN)
                    }
                }
            }
        }
    }

    // private fun String?.toMonospacedText(): Node {
    //     if (this == null) return Text()
    //     return Text(this).also { it.style = "-fx-font-family: monospaced" }
    // }

    /**
     * Opens a modal [Alert] dialog showing this solution's [it.unibo.tuprolog.solve.binaryDecisionDiagram],
     * rendered as Graphviz DOT source inside a [GraphRenderView]; a no-op if the solution has no diagram
     * attached. Invoked when [btnShowBinaryDecisionDiagram] is pressed on a [YesViewPLP].
     */
    fun onShowBinaryDecisionDiagramPressed() {
        solution.binaryDecisionDiagram?.let {
            val dialog = Alert(Alert.AlertType.INFORMATION)
            dialog.title = "Binary Decision Diagram"
            dialog.dialogPane.content = GraphRenderView(it.toDotString())
            dialog.dialogPane.minHeight = Region.USE_PREF_SIZE
            dialog.dialogPane.minWidth = Region.USE_PREF_SIZE
            dialog.graphic = null
            dialog.headerText = null
            dialog.showAndWait()
        }
    }

    // private fun bddToImage(bdd: BinaryDecisionDiagram<*>): Image {
    //     val outputStream = ByteArrayOutputStream()
    //     Graphviz
    //         .fromString(bdd.toDotString())
    //         .render(Format.PNG)
    //         .toOutputStream(outputStream)
    //     val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    //     return Image(inputStream)
    // }
}
