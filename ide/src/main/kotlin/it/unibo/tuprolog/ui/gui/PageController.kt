package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.exception.Warning
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
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
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import org.fxmisc.richtext.CodeArea
import java.net.URL
import java.util.ResourceBundle
import it.unibo.tuprolog.utils.io.File as KtFile
import javafx.event.Event as JavaFxEvent

class PageController(
    private val model: Page,
    private val view: PageView,
    private val appController: ApplicationController
) : AbstractController() {

    @FXML
    private lateinit var txfQuery: TextField

    @FXML
    private lateinit var btnNext: Button

    @FXML
    private lateinit var btnNextAll: Button

    @FXML
    private lateinit var btnStop: Button

    @FXML
    private lateinit var btnReset: Button

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
    private lateinit var lsvWarnings: ListView<Warning>

    private val streamsTabs: Sequence<Tab>
        get() = tabsStreams.tabs.asSequence()

    private fun String.caretLocation(position: Int): Pair<Int, Int> {
        val portion = this.subSequence(0, position)
        val lines = portion.count { it == '\n' } + 1
        val lastLine = portion.lastIndexOf('\n')
        val columns = position - lastLine
        return lines to columns
    }

    private var lastEvent: SolverEvent<*>? = null

    private fun updateContextSensitiveView(event: SolverEvent<*>) {
        if (event.operators != lastEvent?.operators) {
            tbvOperators.items.setAll(event.operators)
            tabOperators.showNotification()
            view.notifyOperators(event.operators)
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
        if (lastEvent?.staticKb?.let { event.staticKb.equals(it, useVarCompleteName = false) } == false) {
            txaStaticKb.text = event.staticKb.pretty()
            tabStaticKb.showNotification()
        }
        if (lastEvent?.dynamicKb?.let { event.dynamicKb.equals(it, useVarCompleteName = false) } == false) {
            txaDynamicKb.text = event.dynamicKb.pretty()
            tabDynamicKb.showNotification()
        }
        lastEvent = event
    }

    private fun updatingContextSensitiveView(event: SolverEvent<*>, action: () -> Unit) {
        action()
        updateContextSensitiveView(event)
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        lsvSolutions.setCellFactory { ListCellView { SolutionView.of(it) } }
        lsvWarnings.setCellFactory { ListCellView { Label(it.message) } }

        tbcFunctor.cellValueFactory = PropertyValueFactory(Operator::functor.name)
        tbcSpecifier.cellValueFactory = PropertyValueFactory(Operator::specifier.name)
        tbcPriority.cellValueFactory = PropertyValueFactory(Operator::priority.name)

        tbcKey.cellValueFactory = PropertyValueFactory(Pair<String, Term>::first.name)
        tbcValue.cellValueFactory = PropertyValueFactory(Pair<String, Term>::second.name)

        trvLibraries.root = TreeItem("Loaded libraries:")

        bindPage(model)

        streamsTabs.forEach { it.hideNotification() }
    }

    private fun bindPage(page: Page) {
        page.onResolutionStarted.bind(this::onResolutionStarted)
        page.onResolutionOver.bind(this::onResolutionOver)
        page.onNewQuery.bind(this::onNewQuery)
        page.onQueryOver.bind(this::onQueryOver)
        page.onNewSolution.bind(this::onNewSolution)
        page.onStdoutPrinted.bind(this::onStdoutPrinted)
        page.onStderrPrinted.bind(this::onStderrPrinted)
        page.onWarning.bind(this::onWarning)
        page.onNewSolver.bind(this::onNewSolver)
        page.onNewStaticKb.bind(this::onNewStaticKb)
        page.onSolveOptionsChanged.bind(this::onSolveOptionsChanged)
        page.onSave.bind(this::onSave)
        page.onReset.bind(this::onReset)
        // TODO do this on page selection and on page solve option modification
//        updateTimeoutView(model.solveOptions.timeout)
    }

    private fun continueResolution(all: Boolean = false) {
        model.next(if (all) Int.MAX_VALUE else 1)
    }

    private fun startNewResolution(all: Boolean = false) {
        model.solve(if (all) Int.MAX_VALUE else 1)
    }

    private fun cleanUpAfterResolution() {
        lsvSolutions.items.clear()
    }

    @FXML
    fun onKeyTypedOnQuery(e: KeyEvent) {
        model.query = txfQuery.text
    }

    @FXML
    fun onNextButtonPressed(e: ActionEvent) {
        if (model.state == Page.Status.IDLE) {
            startNewResolution()
        } else {
            continueResolution()
        }
    }

    @FXML
    fun onStdinChanged(e: KeyEvent) {
        model.stdin = txaStdin.text
    }

    @FXML
    fun onNextAllButtonPressed(e: ActionEvent) {
        if (model.state == Page.Status.IDLE) {
            startNewResolution(true)
        } else {
            continueResolution(true)
        }
    }

    @FXML
    fun onStopButtonPressed(e: ActionEvent) {
        model.stop()
    }

    @FXML
    fun onResetButtonPressed(e: ActionEvent) {
        model.reset()
    }

    private fun propagateCaretMovement(e: JavaFxEvent) {
        (e.source as? CodeArea)?.let {
            appController.notifyCaretPosition(it.text.caretLocation(it.caretPosition))
        }
    }

    @FXML
    fun onActionPerformedOnQuery(e: ActionEvent) {
        startNewResolution()
    }

    //    @FXML
//    lateinit var btnClose: Button

    @FXML
    fun onMousePressedOnCodeArea(e: MouseEvent) {
        propagateCaretMovement(e)
    }

    @FXML
    fun onKeyTypedOnCodeArea(e: KeyEvent) {
        model.theory = view.wholeText
        propagateCaretMovement(e)
        if (e.isControlDown) {
            when (e.character) {
                "+" -> view.fontSize++
                "-" -> view.fontSize--
                else -> {}
            }
        }
    }

    @FXML
    fun onKeyPressedOnCodeArea(e: KeyEvent) {
        model.theory = view.wholeText
        propagateCaretMovement(e)
    }

    private fun onResolutionStarted(e: SolverEvent<Int>) = onUiThread {
        updatingContextSensitiveView(e) {
            btnNext.isDisable = true
            btnNextAll.isDisable = true
            btnStop.isDisable = true
            btnReset.isDisable = true
            prbResolution.isVisible = true
            txfQuery.isDisable = true
            appController.notifyStatus("Solving...")
        }
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
            txaStdin.isDisable = true
            btnNext.text = "Next"
            btnNextAll.text = "All next"
        }
    }

    private fun onQueryOver(e: SolverEvent<Struct>) = onUiThread {
        updatingContextSensitiveView(e) {
            appController.notifyStatus("Idle")
            btnStop.isDisable = true
            txaStdin.isDisable = false
            btnNext.text = "Solve"
            btnNextAll.text = "Solve all"
        }
    }

    private fun onResolutionOver(e: SolverEvent<Int>) = onUiThread {
        updatingContextSensitiveView(e) {
            btnNext.isDisable = false
            btnNextAll.isDisable = false
            prbResolution.isVisible = false
            btnStop.isDisable = false
            btnReset.isDisable = false
            txfQuery.isDisable = false
            appController.notifyStatus("Solution ${e.event}")
        }
    }

    private fun onStdoutPrinted(e: Event<String>) = onUiThread {
        txaStdout.text += e.event
        tabStdout.showNotification()
    }

    private fun onStderrPrinted(e: Event<String>) = onUiThread {
        txaStderr.text += e.event
        tabStderr.showNotification()
    }

    private fun onWarning(e: Event<Warning>) = onUiThread {
        lsvWarnings.items.add(e.event)
        lsvWarnings.scrollTo(e.event)
        tabWarnings.showNotification()
    }

    private fun onSave(e: Event<Pair<PageID, KtFile>>) {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Save file"
        alert.headerText = "File correctly saved"
        alert.contentText = e.event.first.name
        alert.dialogPane.minHeight = Region.USE_PREF_SIZE
        alert.showAndWait()
    }

    private fun onNewSolver(e: SolverEvent<PageID>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onNewStaticKb(e: SolverEvent<PageID>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onSolveOptionsChanged(e: Event<SolveOptions>) {
        appController.notifyTimeoutUpdate(e.event.timeout)
    }

    private fun onReset(e: SolverEvent<PageID>) {
        // currentFileTab?.let { model.setCurrentFile(it.wholeText) }
        lsvWarnings.items.clear()
        txaStdout.clear()
        txaStderr.clear()
        lsvSolutions.items.clear()
    }
}