package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TimeOutException
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import java.io.IOException

sealed class SolutionView<T, S : Solution>(
    protected val solution: S,
) : VBox() {
    companion object {
        private const val FXML = "SolutionView.fxml"

        private val ITEM_MARGIN = Insets(0.0, 0.0, 0.0, 55.0)

        val COLOR_YES: Paint = Paint.valueOf("LIME")
        val COLOR_NO: Paint = Paint.valueOf("GRAY")
        val COLOR_HALT: Paint = Paint.valueOf("RED")
        val COLOR_TIMEOUT: Paint = Paint.valueOf("GOLD")

        fun of(solution: Solution): SolutionView<*, *> =
            solution.whenIs(
                yes = { YesView(it) },
                no = { NoView(it) },
                halt = { HaltView(it) },
            )
    }

    protected val formatter = TermFormatter.prettyExpressions()

    init {
        val loader = FXMLLoader(SolutionView::class.java.getResource(FXML))
        loader.setController(this)
        loader.setRoot(this)

        try {
            loader.load<BorderPane>()
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

    class YesView(
        solution: Solution.Yes,
    ) : SolutionView<Pair<Var, Term>, Solution.Yes>(solution) {
        init {
            status.text = "yes:"
            led.fill = COLOR_YES
            query.text = solution.solvedQuery.format(formatter)
            solution.substitution.map { it.toPair() }.forEach {
                val assignment = AssignmentView(it, formatter)
                children.add(assignment)
                setMargin(assignment, ITEM_MARGIN)
            }
        }
    }

    class NoView(
        solution: Solution.No,
    ) : SolutionView<String, Solution.No>(solution) {
        init {
            led.fill = COLOR_NO
            status.text = "no."
            query.isVisible = false
        }
    }

    class HaltView(
        solution: Solution.Halt,
    ) : SolutionView<String, Solution.Halt>(solution) {
        init {
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
}
