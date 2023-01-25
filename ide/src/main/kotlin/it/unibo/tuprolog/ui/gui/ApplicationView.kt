package it.unibo.tuprolog.ui.gui

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.fxmisc.richtext.LineNumberFactory
import java.io.IOException

class ApplicationView(
    private val app: Application
) : Parent() {
    companion object {
        private const val FXML = "TuPrologIDEView.fxml"
    }

    val controller: ApplicationController = ApplicationController(app)

    init {
        val loader = FXMLLoader(PageView::class.java.getResource(FXML))
        loader.setController(controller)
        loader.setRoot(this)

        loader.load<Parent>()
    }
}
