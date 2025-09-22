package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import java.io.IOException

class AssignmentView(
    variable: Var,
    value: Term,
    formatter: TermFormatter = TermFormatter.prettyExpressions(),
) : HBox() {
    companion object {
        private const val FXML = "AssignmentView.fxml"
    }

    init {
        val loader = FXMLLoader(AssignmentView::class.java.getResource(FXML))
        loader.setController(this)
        loader.setRoot(this)

        try {
            loader.load<HBox>()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }

        this.variable.text = variable.format(formatter)
        this.value.text = value.format(formatter)
    }

    constructor(assignment: Pair<Var, Term>, formatter: TermFormatter = TermFormatter.prettyExpressions()) :
        this(assignment.first, assignment.second, formatter)

    @FXML
    lateinit var variable: Label

    @FXML
    lateinit var operator: Label

    @FXML
    lateinit var value: Label
}
