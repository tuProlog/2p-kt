package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Tab
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import java.io.File
import java.io.IOException
import kotlin.math.max

@Suppress("UNUSED_PARAMETER")
class FileTabView(
    file: File,
    private val model: TuPrologIDEModel,
    private val ideController: TuPrologIDEController,
    initialText: String = "",
) : Tab() {
    companion object {
        private const val FXML = "FileTabView.fxml"
        private const val MIN_FONT_SIZE = 13
        private const val DEFAULT_FONT_SIZE = 16
    }

    private val syntaxColoring: SyntaxColoring

    private var fontSize: Int = MIN_FONT_SIZE
        set(value) {
            field = max(value, MIN_FONT_SIZE)
            codeArea.style = "-fx-font-size: $value"
        }

    init {
        val loader = FXMLLoader(FileTabView::class.java.getResource(FXML))
        loader.setController(this)
        loader.setRoot(this)

        try {
            loader.load<Tab>()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }

        fontSize = DEFAULT_FONT_SIZE

        codeArea.appendText(initialText)
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        syntaxColoring = SyntaxColoring(codeArea)
        syntaxColoring.activate()

        text = file.name
    }

    @FXML
    lateinit var codeArea: CodeArea

    var wholeText: String
        get() = codeArea.text
        set(value) {
            codeArea.replaceText(value)
            model.setFile(file, codeArea.text)
        }

    fun notifyOperators(operators: OperatorSet) {
        syntaxColoring.operators = operators
        syntaxColoring.applyHighlightingNow()
    }

    fun updateSyntaxColoring() {
        syntaxColoring.applyHighlightingNow()
    }

//    @FXML
//    lateinit var btnClose: Button

    @FXML
    fun onTabSelectionChanged(e: Event) {
        if (isSelected) {
            model.selectFile(file)
        }
    }

    var file: File = file
        get
        set(value) {
            field = value
            text = value.name
        }

    @FXML
    fun onMousePressedOnCodeArea(e: MouseEvent) {
        ideController.onMouseClickedOnCurrentFile(e)
    }

    @FXML
    fun onKeyTypedOnCodeArea(e: KeyEvent) {
        model.setFile(file, wholeText)
        ideController.onKeyTypedOnCurrentFile(e)
        if (e.isControlDown) {
            when (e.character) {
                "+" -> fontSize++
                "-" -> fontSize--
                else -> {}
            }
        }
    }

    @FXML
    fun onKeyPressedOnCodeArea(e: KeyEvent) {
        model.setFile(file, wholeText)
        ideController.onKeyPressedOnCurrentFile(e)
    }
}
