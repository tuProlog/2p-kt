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

/**
 * A [Tab] hosting a RichTextFX [CodeArea] editor for a single Prolog [file], as added to the IDE's file tab
 * pane by [TuPrologIDEController] whenever [TuPrologIDEModel.onFileLoaded] fires. It keeps the model's
 * in-memory copy of the file (see [TuPrologIDEModel.setFile]) in sync with the editor's text on every
 * keystroke, applies [SyntaxColoring] to the editor, and forwards caret/keyboard events to [ideController]
 * so the status bar can be updated.
 */
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

    /** The full text currently in the editor;
     * setting it replaces the editor's content and updates the model's copy of [file]. */
    var wholeText: String
        get() = codeArea.text
        set(value) {
            codeArea.replaceText(value)
            model.setFile(file, codeArea.text)
        }

    /** Updates the [SyntaxColoring]'s keyword set to [operators] and re-highlights the editor immediately. */
    fun notifyOperators(operators: OperatorSet) {
        syntaxColoring.operators = operators
        syntaxColoring.applyHighlightingNow()
    }

    /** Forces an immediate re-highlighting of the editor's current text, e.g. right after this tab is selected. */
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

    /** The [File] this tab's editor content is associated with in the model;
     * setting it also renames the tab's label. */
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
