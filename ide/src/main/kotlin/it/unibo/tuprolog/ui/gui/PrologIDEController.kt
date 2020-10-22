package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.theory.Theory
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressBar
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.stage.Stage
import org.fxmisc.richtext.CodeArea
import java.io.File
import java.net.URL
import java.util.ResourceBundle

@Suppress("UNUSED_PARAMETER")
class PrologIDEController : Initializable {

    private val model = PrologIDEModel.of()

    @FXML
    private lateinit var root: Parent

    @FXML
    private lateinit var lblStatus: Label

    @FXML
    private lateinit var lblCaret: Label

    @FXML
    private lateinit var txfQuery: TextField

    @FXML
    private lateinit var btnNext: Button

    @FXML
    private lateinit var btnNextAll: Button

    @FXML
    private lateinit var btnStop: Button

    @FXML
    private lateinit var btnNewFile: MenuItem

    @FXML
    private lateinit var btnOpenFile: MenuItem

    @FXML
    private lateinit var btnCloseFile: MenuItem

    @FXML
    private lateinit var btnSaveFile: MenuItem

    @FXML
    private lateinit var btnReloadFile: MenuItem

    @FXML
    private lateinit var btnSettings: MenuItem

    @FXML
    private lateinit var btnQuit: MenuItem

    @FXML
    private lateinit var btnAbout: MenuItem

    @FXML
    private lateinit var tabsFiles: TabPane

    @FXML
    private lateinit var tabsStreams: TabPane

    @FXML
    private lateinit var tabSolutions: Tab

    @FXML
    private lateinit var tabStdin: Tab

    @FXML
    private lateinit var tabStdout: Tab

    @FXML
    private lateinit var tabStderr: Tab

    @FXML
    private lateinit var tabWarnings: Tab

    @FXML
    private lateinit var tabOperators: Tab

    @FXML
    private lateinit var tabFlags: Tab

    @FXML
    private lateinit var tabLibraries: Tab

    @FXML
    private lateinit var tabStaticKb: Tab

    @FXML
    private lateinit var tabDynamicKb: Tab

    @FXML
    private lateinit var tbvOperators: TableView<Operator>

    @FXML
    private lateinit var tbcFunctor: TableColumn<Operator, String>

    @FXML
    private lateinit var tbcPriority: TableColumn<Operator, Int>

    @FXML
    private lateinit var tbvFlags: TableView<Pair<String, Term>>

    @FXML
    private lateinit var tbcKey: TableColumn<Operator, String>

    @FXML
    private lateinit var tbcValue: TableColumn<Operator, Term>

    @FXML
    private lateinit var tbcSpecifier: TableColumn<Operator, Specifier>

    @FXML
    private lateinit var trvLibraries: TreeView<String>

    @FXML
    private lateinit var txaStaticKb: TextArea

    @FXML
    private lateinit var txaDynamicKb: TextArea

    @FXML
    private lateinit var prbResolution: ProgressBar

    @FXML
    private lateinit var txaStdin: TextArea

    @FXML
    private lateinit var txaStdout: TextArea

    @FXML
    private lateinit var txaStderr: TextArea

    @FXML
    private lateinit var lsvSolutions: ListView<Solution>

    @FXML
    private lateinit var lsvWarnings: ListView<PrologWarning>

    private val stage: Stage get() = root.scene.window as Stage

    private inline fun onUiThread(crossinline f: () -> Unit) =
        Platform.runLater {
            f()
        }

    @FXML
    override fun initialize(location: URL, resources: ResourceBundle?) {
        model.onResolutionStarted.subscribe(this::onResolutionStarted)
        model.onResolutionOver.subscribe(this::onResolutionOver)
        model.onNewQuery.subscribe(this::onNewQuery)
        model.onQueryOver.subscribe(this::onQueryOver)
        model.onNewSolution.subscribe(this::onNewSolution)
        model.onStdoutPrinted.subscribe(this::onStdoutPrinted)
        model.onStderrPrinted.subscribe(this::onStderrPrinted)
        model.onWarning.subscribe(this::onWarning)
        model.onError.subscribe(this::onError)
        model.onFileLoaded.subscribe(this::onFileLoaded)
        model.onNewSolver.subscribe(this::onNewSolver)
        model.onNewStaticKb.subscribe(this::onNewStaticKb)

        lsvSolutions.setCellFactory { ListCellView { SolutionView.of(it) } }
        lsvWarnings.setCellFactory { ListCellView { Label(it.message) } }

        tbcFunctor.cellValueFactory = PropertyValueFactory(Operator::functor.name)
        tbcSpecifier.cellValueFactory = PropertyValueFactory(Operator::specifier.name)
        tbcPriority.cellValueFactory = PropertyValueFactory(Operator::priority.name)

        tbcKey.cellValueFactory = PropertyValueFactory(Pair<String, Term>::first.name)
        tbcValue.cellValueFactory = PropertyValueFactory(Pair<String, Term>::second.name)

        trvLibraries.root = TreeItem("Loaded libraries:")

        model.newFile()
        model.reset()
    }

    private fun onNewSolver(e: SolverEvent<Unit>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onNewStaticKb(e: SolverEvent<Unit>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onFileLoaded(e: Pair<File, String>) = onUiThread {
        tabsFiles.tabs.add(FileTabView(e.first, model, this, e.second))
    }

    private fun onStdoutPrinted(output: String) = onUiThread {
        txaStdout.text += output
        tabStdout.showNotification()
    }

    private fun Tab.showNotification() {
        if (!isSelected && !text.endsWith("*")) {
            text += "*"
        }
    }

    private fun Tab.hideNotification() {
        if (text.endsWith("*")) {
            text = text.substringBefore('*')
        }
    }

    private var lastEvent: SolverEvent<*>? = null

    private fun Theory.pretty(): String =
        if (size > 0) {
            clauses.asSequence().joinToString(".\n", postfix = ".") {
                it.format(TermFormatter.prettyExpressions())
            }
        } else {
            ""
        }

    private fun updateContextSensitiveView(event: SolverEvent<*>) {
        if (event.operators != lastEvent?.operators) {
            tbvOperators.items.setAll(event.operators)
            tabOperators.showNotification()
        }
        if (event.flags != lastEvent?.flags) {
            tbvFlags.items.setAll(event.flags.map { it.toPair() })
            tabFlags.showNotification()
        }
        if (event.libraries != lastEvent?.libraries) {
            with(trvLibraries.root) {
                children.setAll(event.libraries.libraries.map { LibraryView(it) })
                if (children.isNotEmpty()) {
                    isExpanded = true
                }
            }
            tabLibraries.showNotification()
        }
        if (event.staticKb != lastEvent?.staticKb) {
            txaStaticKb.text = event.staticKb.pretty()
            tabStaticKb.showNotification()
        }
        if (event.dynamicKb != lastEvent?.dynamicKb) {
            txaDynamicKb.text = event.dynamicKb.pretty()
            tabDynamicKb.showNotification()
        }
    }

    private fun updatingContextSensitiveView(event: SolverEvent<*>, action: () -> Unit) {
        action()
        updateContextSensitiveView(event)
    }

    private fun onStderrPrinted(output: String) = onUiThread {
        txaStderr.text += output
        tabStderr.showNotification()
    }

    private fun onWarning(warning: PrologWarning) = onUiThread {
        lsvWarnings.items.add(warning)
        lsvWarnings.scrollTo(warning)
        tabWarnings.showNotification()
    }

    private fun onError(exception: TuPrologException) = onUiThread {
        val dialog = Alert(Alert.AlertType.ERROR)
        dialog.headerText = exception::class.java.simpleName
        dialog.title = "Error"
        dialog.contentText = exception.message?.capitalize()
        dialog.dialogPane.minHeight = Region.USE_PREF_SIZE
        dialog.showAndWait()
    }

    private fun onNewSolution(e: SolverEvent<Solution>) = onUiThread {
        updatingContextSensitiveView(e) {
            lsvSolutions.items.add(e.event)
            lsvSolutions.scrollTo(e.event)
            tabSolutions.showNotification()
        }
    }

    private fun onNewQuery(e: SolverEvent<Struct>) = onUiThread {
        updatingContextSensitiveView(e) {
            cleanUpAfterResolution()
            btnStop.isDisable = false
        }
    }

    private fun onQueryOver(e: SolverEvent<Struct>) = onUiThread {
        updatingContextSensitiveView(e) {
            lblStatus.text = "Idle"
            btnStop.isDisable = true
        }
    }

    private fun onResolutionStarted(e: SolverEvent<Int>) = onUiThread {
        updatingContextSensitiveView(e) {
            btnNext.isDisable = true
            btnNextAll.isDisable = true
            btnStop.isDisable = true
            prbResolution.isVisible = true
            txfQuery.isDisable = true
            lblStatus.text = "Solving..."
        }
    }

    private fun onResolutionOver(e: SolverEvent<Int>) = onUiThread {
        updatingContextSensitiveView(e) {
            btnNext.isDisable = false
            btnNextAll.isDisable = false
            prbResolution.isVisible = false
            btnStop.isDisable = false
            txfQuery.isDisable = false
            lblStatus.text = "Solution ${e.event}"
        }
    }

    @FXML
    fun onKeyTypedOnCurrentFile(e: KeyEvent) {
        (e.source as? CodeArea)?.let {
            onCaretMovedIn(it)
        }
    }

    @FXML
    fun onKeyTypedOnQuery(e: KeyEvent) {
        model.query = txfQuery.text
    }

    @FXML
    fun onNextButtonPressed(e: ActionEvent) {
        if (model.state == PrologIDEModel.State.IDLE) {
            startNewResolution()
        } else {
            continueResolution()
        }
    }

    @FXML
    fun onNextAllButtonPressed(e: ActionEvent) {
        if (model.state == PrologIDEModel.State.IDLE) {
            startNewResolution(true)
        } else {
            continueResolution(true)
        }
    }

    @FXML
    fun onStopButtonPressed(e: ActionEvent) {
        model.stop()
    }

    private fun continueResolution(all: Boolean = false) {
        if (all) {
            model.nextAll()
        } else {
            model.next()
        }
    }

    private fun startNewResolution(all: Boolean = false) {
        if (all) {
            model.solveAll()
        } else {
            model.solve()
        }
    }

    private fun cleanUpAfterResolution() {
        lsvSolutions.items.clear()
    }

    @FXML
    fun onQuitRequested(e: ActionEvent) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Close tuProlog IDE"
        alert.headerText = "Confirmation"
        alert.contentText = "Are you absolutely sure you wanna close the IDE?"
        alert.showAndWait().ifPresent {
            if (it == ButtonType.OK) {
                stage.close()
            }
        }
    }

    private fun onCaretMovedIn(area: CodeArea) {
        val (l, c) = area.text.caretLocation(area.caretPosition)
        lblCaret.text = "Line: $l | Column: $c"
    }

    private fun String.caretLocation(position: Int): Pair<Int, Int> {
        val portion = this.subSequence(0, position)
        val lines = portion.count { it == '\n' } + 1
        val lastLine = portion.lastIndexOf('\n')
        val columns = position - lastLine
        return lines to columns
    }

    @FXML
    fun onMouseClickedOnCurrentFile(e: MouseEvent) {
        (e.source as? CodeArea)?.let {
            onCaretMovedIn(it)
        }
    }

    @FXML
    fun onKeyPressedOnCurrentFile(e: KeyEvent) {
        (e.source as? CodeArea)?.let {
            onCaretMovedIn(it)
        }
    }

    @FXML
    fun onActionPerformedOnQuery(e: ActionEvent) {
        startNewResolution()
    }

    @FXML
    fun onTabSelectionChanged(e: Event) {
        val tab = e.source as Tab
        if (tab.isSelected) {
            tab.hideNotification()
        }
    }

    @FXML
    fun onNewFilePressed(e: ActionEvent) {
        model.newFile()
    }

    @FXML
    fun onAbout(e: ActionEvent) {
        val dialog = Alert(Alert.AlertType.INFORMATION)
        dialog.title = "About"
        dialog.headerText = "tuProlog IDE v${Info.VERSION}"
        dialog.dialogPane.graphic = ImageView(TUPROLOG_LOGO).also {
            it.fitWidth = TUPROLOG_LOGO.width * 0.3
            it.fitHeight = TUPROLOG_LOGO.height * 0.3
        }
        dialog.contentText =
            """
            |Running on:
            |  - 2P-Kt v${Info.VERSION}
            |  - JVM v${System.getProperty("java.version")}
            |  - JavaFX v${System.getProperties().get("javafx.runtime.version")}
            """.trimMargin()
        dialog.showAndWait()
    }
}
