package it.unibo.tuprolog.ui.gui

import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class GraphRenderView(
    private val dotGraph: String,
) : VBox() {

    companion object {
        private const val FXML = "GraphRenderView.fxml"
    }

    init {
        val loader = FXMLLoader(GraphRenderView::class.java.getResource(FXML))
        loader.setController(this)
        loader.setRoot(this)

        try {
            loader.load<BorderPane>()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
        btnCopy.setOnAction { this.copyToClipboard() }
        btnRender.setOnAction { this.renderToImage() }
        btnSave.setOnAction { this.saveToFile() }
        btnSave.isDisable = true
        textArea.text = dotGraph

        val outputStream = ByteArrayOutputStream()
        Graphviz
            .fromString(dotGraph)
            .render(Format.PNG)
            .toOutputStream(outputStream)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        imageView.image = Image(inputStream)
        imageView.isVisible = true
    }

    @FXML
    lateinit var btnCopy: Button

    @FXML
    lateinit var btnRender: Button

    @FXML
    lateinit var btnSave: Button

    @FXML
    lateinit var textArea: TextArea

    @FXML
    lateinit var imageView: ImageView

    private fun copyToClipboard() {
    }

    private fun renderToImage() {
    }

    private fun saveToFile() {
    }
}
