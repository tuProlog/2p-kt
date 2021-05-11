package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.theory.Theory
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.ProgressBar
import javafx.scene.control.Slider
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
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.fxmisc.richtext.CodeArea
import java.io.File
import java.lang.Math.pow
import java.net.URL
import java.util.ResourceBundle
import kotlin.math.log10
import kotlin.math.round

@Suppress("UNUSED_PARAMETER")
class PrologIDEController : Initializable {

    private val model = PrologIDEModel.of()

    private var onClose = {}

    private var onAbout = {}

    @FXML
    private lateinit var sldTimeout: Slider

    @FXML
    private lateinit var lblTimeout: Label

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
    private lateinit var btnReset: Button

    @FXML
    private lateinit var btnNewFile: MenuItem

    @FXML
    private lateinit var btnOpenFile: MenuItem

    @FXML
    private lateinit var btnCloseFile: MenuItem

    @FXML
    private lateinit var btnSaveFile: MenuItem

    @FXML
    private lateinit var btnSaveFileAs: MenuItem

    @FXML
    private lateinit var btnReloadFile: MenuItem

    @FXML
    private lateinit var btnSettings: MenuItem

    @FXML
    private lateinit var btnQuit: MenuItem

    @FXML
    private lateinit var btnUndo: MenuItem

    @FXML
    private lateinit var btnRedo: MenuItem

    @FXML
    private lateinit var btnCut: MenuItem

    @FXML
    private lateinit var btnCopy: MenuItem

    @FXML
    private lateinit var btnPaste: MenuItem

    @FXML
    private lateinit var btnDelete: MenuItem

    @FXML
    private lateinit var btnSelectAll: MenuItem

    @FXML
    private lateinit var btnUnselectAll: MenuItem

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

    private val fileTabs: Sequence<FileTabView>
        get() = tabsFiles.tabs.asSequence().filterIsInstance<FileTabView>()

    private val streamsTabs: Sequence<Tab>
        get() = tabsStreams.tabs.asSequence()

    private val currentFileTab: FileTabView?
        get() = fileTabs.firstOrNull { it.file == model.currentFile }

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
        model.onFileClosed.subscribe(this::onFileClosed)
        model.onFileSelected.subscribe(this::onFileSelected)
        model.onNewSolver.subscribe(this::onNewSolver)
        model.onNewStaticKb.subscribe(this::onNewStaticKb)
        model.onTimeoutChanged.subscribe(this::onTimeoutChanged)

        sldTimeout.valueProperty().addListener(ChangeListener { _, _, value -> onTimeoutSliderMoved(value) })

        updateTimeoutView(model.timeout)

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

        Platform.runLater {
            streamsTabs.forEach { it.hideNotification() }
        }
    }

    private fun onTimeoutSliderMoved(value: Number) {
        model.timeout = round(pow(10.0, value.toDouble() / 1.0e5)).toLong()
    }

    private fun onTimeoutChanged(newTimeout: TimeDuration) {
        updateTimeoutView(newTimeout)
    }

    private fun updateTimeoutView(timeout: TimeDuration) {
        val seconds = timeout / 1000.0
        lblTimeout.text = String.format("%.3f s", seconds)
    }

    private fun onNewSolver(e: SolverEvent<Unit>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onNewStaticKb(e: SolverEvent<Unit>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onFileLoaded(e: Pair<File, String>) = onUiThread {
        tabsFiles.tabs.add(FileTabView(e.first, model, this, e.second))
        handleSomeOpenFiles()
    }

    private fun onFileClosed(e: File) = onUiThread {
        fileTabs.firstOrNull { it.file == e }?.let {
            tabsFiles.tabs -= it
            handleNoMoreOpenFiles()
        }
    }

    private fun onFileSelected(e: File) = onUiThread {
        fileTabs.firstOrNull { it.file == e }?.let {
            it.updateSyntaxColoring()
            tabsFiles.selectionModel.select(it)
        }
    }

    private fun handleNoMoreOpenFiles() {
        if (fileTabs.none()) {
            btnCloseFile.isDisable = true
            btnSaveFile.isDisable = true
            btnSaveFileAs.isDisable = true
            btnReloadFile.isDisable = true
            lblStatus.text = "Line: - | Column: -"
        }
    }

    private fun handleSomeOpenFiles() {
        if (fileTabs.any()) {
            btnCloseFile.isDisable = false
            btnSaveFile.isDisable = false
            btnSaveFileAs.isDisable = false
            btnReloadFile.isDisable = false
        }
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
            clauses.joinToString(".\n", postfix = ".") {
                it.format(TermFormatter.prettyExpressions())
            }
        } else {
            ""
        }

    private fun updateContextSensitiveView(event: SolverEvent<*>) {
        if (event.operators != lastEvent?.operators) {
            tbvOperators.items.setAll(event.operators)
            tabOperators.showNotification()
            fileTabs.forEach {
                it.notifyOperators(event.operators)
            }
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

    private fun onError(exception: Exception) = onUiThread {
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
            txaStdin.isDisable = true
            btnNext.text = "Next"
            btnNextAll.text = "All next"
        }
    }

    private fun onQueryOver(e: SolverEvent<Struct>) = onUiThread {
        updatingContextSensitiveView(e) {
            lblStatus.text = "Idle"
            btnStop.isDisable = true
            txaStdin.isDisable = false
            btnNext.text = "Solve"
            btnNextAll.text = "Solve all"
        }
    }

    private fun onResolutionStarted(e: SolverEvent<Int>) = onUiThread {
        updatingContextSensitiveView(e) {
            btnNext.isDisable = true
            btnNextAll.isDisable = true
            btnStop.isDisable = true
            btnReset.isDisable = true
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
            btnReset.isDisable = false
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
    fun onStdinChanged(e: KeyEvent) {
        model.setStdin(txaStdin.text)
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

    @FXML
    fun onResetButtonPressed(e: ActionEvent) {
        model.reset()
        // currentFileTab?.let { model.setCurrentFile(it.wholeText) }
        txaStdout.clear()
        txaStderr.clear()
        lsvSolutions.items.clear()
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
        this.onClose()
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
    fun onOpenFilePressed(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Prolog file", "*.pl", "*.2p"),
            FileChooser.ExtensionFilter("Text file", "*.txt"),
            FileChooser.ExtensionFilter("Any file", "*"),
        )
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        fileChooser.title = "Open file..."
        val file = fileChooser.showOpenDialog(stage)
        model.loadFile(file)
    }

    @FXML
    fun onCloseFilePressed(e: ActionEvent) {
        model.closeFile(model.currentFile!!)
    }

    @FXML
    fun onSaveFilePressed(e: ActionEvent) {
        try {
            model.saveFile(model.currentFile!!)
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.title = "Save file"
            alert.headerText = "File correctly saved"
            alert.contentText = model.currentFile?.canonicalPath
            alert.dialogPane.minHeight = Region.USE_PREF_SIZE
            alert.showAndWait()
        } catch (e: Exception) {
            onError(e)
        }
    }

    @FXML
    fun onSaveFileAsPressed(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Prolog file", "*.pl"),
            FileChooser.ExtensionFilter("Text file", "*.txt"),
            FileChooser.ExtensionFilter("2P file", "*.2p"),
        )
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        fileChooser.title = "Save file as..."
        val file = fileChooser.showSaveDialog(stage)
        model.renameFile(model.currentFile!!, file)
        model.saveFile(file)
        currentFileTab?.file = file
    }

    @FXML
    fun onReloadFilePressed(e: ActionEvent) {
        currentFileTab?.let {
            it.wholeText = model.currentFile!!.readText()
        }
    }

    @FXML
    fun onUndoPressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.undo()
        }
    }

    @FXML
    fun onRedoPressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.redo()
        }
    }

    @FXML
    fun onCutPressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.cut()
        }
    }

    @FXML
    fun onCopyPressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.copy()
        }
    }

    @FXML
    fun onPastePressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.paste()
        }
    }

    @FXML
    fun onDeletePressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.deleteText(it.codeArea.selection)
        }
    }

    @FXML
    fun onSelectAllPressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.selectAll()
        }
    }

    @FXML
    fun onUnselectAllPressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.deselect()
        }
    }

    @FXML
    fun onSettingsPressed(e: ActionEvent) {
    }

    @FXML
    fun onAbout(e: ActionEvent) {
        this.onAbout()
    }

    fun customizeModel(setup: ModelConfigurator) = setup(model)

    fun addTab(tab: Tab) {
        this.tabsStreams.tabs.add(tab)
    }

    fun setOnClose(onClose: () -> Unit) {
        this.onClose = onClose
    }

    fun setOnAbout(onAbout: () -> Unit) {
        this.onAbout = onAbout
    }
}
