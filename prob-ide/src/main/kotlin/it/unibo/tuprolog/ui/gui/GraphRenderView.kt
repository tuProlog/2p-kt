package it.unibo.tuprolog.ui.gui

import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.CompletableFuture

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
        btnSave.isVisible = false
        imageView.isVisible = false
        textArea.text = dotGraph
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

    @FXML
    lateinit var labelMsg: Label

    @FXML
    lateinit var progressBar: ProgressBar

    private var imageBytes: ByteArray? = null

    private fun copyToClipboard() {
        val clipboard: Clipboard = Clipboard.getSystemClipboard()
        val content = ClipboardContent()
        content.putString(dotGraph)
        content.putHtml(dotGraph)
        clipboard.setContent(content)
        labelMsg.text = "Text has been copied to clipboard."
        labelMsg.isVisible = true
    }

    private fun renderToImage() {
        progressBar.isVisible = true
        labelMsg.isVisible = false
        this.let {
            CompletableFuture.supplyAsync {
                val outputStream = ByteArrayOutputStream()
                Graphviz
                    .fromString(dotGraph)
                    .render(Format.PNG)
                    .toOutputStream(outputStream)
                imageBytes = outputStream.toByteArray()

                Platform.runLater {
                    imageView.image = Image(ByteArrayInputStream(imageBytes))
                    imageView.isVisible = true
                    btnSave.isVisible = true
                    progressBar.isVisible = false
                    it.scene.window.sizeToScene()
                }
            }
        }
    }

    private fun saveToFile() {
        if (imageBytes != null && imageView.isVisible) {
            val fileChooser = FileChooser()
            fileChooser.title = "Save Image"
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("PNG image file", "*.png"),
                FileChooser.ExtensionFilter("All files", "*.*")
            )
            fileChooser.initialFileName = "bdd-${System.currentTimeMillis()}.png"
            val file = fileChooser.showSaveDialog(scene.window)
            file?.writeBytes(imageBytes!!)
        }
    }
}
