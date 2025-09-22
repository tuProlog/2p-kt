package it.unibo.tuprolog.ui.gui

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
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
            loader.load<VBox>()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }

        btnCopy.setOnAction { this.copyToClipboard() }
        btnRender.setOnAction { this.renderToImage() }
        btnSave.setOnAction { this.saveToFile() }
        btnSave.isVisible = false
        imageView.isVisible = false
        textArea.text = dotGraph
        btnRender.isDisable = !GraphvizRenderer.isAvailable

        if (!GraphvizRenderer.isReady) {
            showMessage(
                "The image renderer is still initializing...\n" +
                    "Close and reopen this panel in few seconds.",
                true,
            )
        } else if (!GraphvizRenderer.isAvailable) {
            showMessage(
                "The image renderer is not available.\n" +
                    "Please check the docs at: https://github.com/nidi3/graphviz-java",
                true,
            )
        } else {
            showMessage("The image rendered is ready.", false)
        }
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
        showMessage("Text has been copied to clipboard.", false)
    }

    private fun renderToImage() {
        if (!GraphvizRenderer.isAvailable) {
            return
        }

        progressBar.isVisible = true
        showMessage("Rendering graph image...", false)
        this.let {
            CompletableFuture.supplyAsync {
                val outputStream = ByteArrayOutputStream()
                GraphvizRenderer.renderAsPNG(dotGraph, outputStream)
                imageBytes = outputStream.toByteArray()

                Platform.runLater {
                    imageView.image = Image(ByteArrayInputStream(imageBytes))
                    imageView.isVisible = true
                    btnSave.isVisible = true
                    progressBar.isVisible = false
                    hideMessage()
                    it.scene.window.sizeToScene()
                }
            }
        }
    }

    private fun saveToFile() {
        imageBytes?.let {
            if (imageView.isVisible) {
                val fileChooser = FileChooser()
                fileChooser.title = "Save Image"
                fileChooser.extensionFilters.addAll(
                    FileChooser.ExtensionFilter("PNG image file", "*.png"),
                    FileChooser.ExtensionFilter("All files", "*.*"),
                )
                fileChooser.initialFileName = "bdd-${System.currentTimeMillis()}.png"
                val file = fileChooser.showSaveDialog(scene.window)
                showMessage("Saving image to file...", false)
                file?.writeBytes(it)
                showMessage("File saved successfully.", false)
            }
        }
    }

    private fun showMessage(
        text: String,
        error: Boolean,
    ) {
        labelMsg.text = text
        labelMsg.textFill = Color.color(if (error) 1.0 else 0.0, 0.0, 0.0)
        labelMsg.isVisible = true
    }

    private fun hideMessage() {
        labelMsg.isVisible = false
    }
}
