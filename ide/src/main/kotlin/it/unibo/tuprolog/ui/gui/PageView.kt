package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.operators.OperatorSet
import javafx.fxml.FXMLLoader
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import kotlin.math.max

@Suppress("UNUSED_PARAMETER")
class PageView(
    private val page: Page,
    private val appController: ApplicationController,
    initialText: String = ""
) : Tab() {

    companion object {
        private const val FXML = "PageView.fxml"
        private const val MIN_FONT_SIZE = 13
        private const val DEFAULT_FONT_SIZE = 16
    }

    val pageID: PageID
        get() = page.id

    val codeArea: CodeArea

    private val syntaxColoring: SyntaxColoring

    var fontSize: Int = MIN_FONT_SIZE
        set(value) {
            field = max(value, MIN_FONT_SIZE)
            codeArea.style = "-fx-font-size: $value"
        }

    init {
        val loader = FXMLLoader(PageView::class.java.getResource(FXML))
        loader.setController(PageController(page, this, appController))
        loader.setRoot(this)

        val root = loader.load<BorderPane>()
        codeArea = root.lookup("#codeArea") as CodeArea

        fontSize = DEFAULT_FONT_SIZE

        codeArea.appendText(initialText)
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        syntaxColoring = SyntaxColoring(codeArea)
        syntaxColoring.activate()

        text = page.id.name
    }

    var wholeText: String
        get() = codeArea.text
        set(value) {
            codeArea.replaceText(value)
            page.theory = codeArea.text
        }

    fun notifyOperators(operators: OperatorSet) {
        syntaxColoring.operators = operators
        syntaxColoring.applyHighlightingNow()
    }

    fun updateSyntaxColoring() {
        syntaxColoring.applyHighlightingNow()
    }
}
