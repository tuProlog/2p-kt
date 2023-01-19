package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.utils.io.JvmFile
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
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
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.fxmisc.richtext.CodeArea
import java.io.File
import java.net.URL
import java.time.Duration
import java.util.Locale
import java.util.ResourceBundle
import kotlin.math.pow
import kotlin.math.round
import kotlin.system.exitProcess
import it.unibo.tuprolog.utils.io.File as KtFile
import javafx.event.Event as JavaFxEvent

@Suppress("UNUSED_PARAMETER", "unused")
class TuPrologIDEController : Initializable {

    private val model = TuPrologIDEModel()

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
    private lateinit var lsvWarnings: ListView<Warning>

    private val stage: Stage get() = root.scene.window as Stage

    private val fileTabs: Sequence<PageView>
        get() = tabsFiles.tabs.asSequence().filterIsInstance<PageView>()

    private val streamsTabs: Sequence<Tab>
        get() = tabsStreams.tabs.asSequence()

    private val currentFileTab: PageView?
        get() = model.currentPage?.let { tabForFile(it.id) }

    private fun tabForFile(page: PageID): PageView? =
        fileTabs.firstOrNull { it.pageID == page }

    @FXML
    override fun initialize(location: URL, resources: ResourceBundle?) {
        model.onStart.bind(this::onStart)
        model.onError.bind(this::onError)
        model.onPageCreated.bind(this::onPageCreated)
        model.onPageLoaded.bind(this::onFileLoaded)
        model.onPageClosed.bind(this::onPageClosed)
        model.onPageSelected.bind(this::onFileSelected)

        model.onQuit.bind(this::onQuit)

        sldTimeout.valueProperty().addListener { _, _, value -> onTimeoutSliderMoved(value) }

        lsvSolutions.setCellFactory { ListCellView { SolutionView.of(it) } }
        lsvWarnings.setCellFactory { ListCellView { Label(it.message) } }

        tbcFunctor.cellValueFactory = PropertyValueFactory(Operator::functor.name)
        tbcSpecifier.cellValueFactory = PropertyValueFactory(Operator::specifier.name)
        tbcPriority.cellValueFactory = PropertyValueFactory(Operator::priority.name)

        tbcKey.cellValueFactory = PropertyValueFactory(Pair<String, Term>::first.name)
        tbcValue.cellValueFactory = PropertyValueFactory(Pair<String, Term>::second.name)

        trvLibraries.root = TreeItem("Loaded libraries:")

        onUiThread {
            streamsTabs.forEach { it.hideNotification() }
        }
    }

    private fun onPageCreated(e: Event<Page>) = onUiThread {
        e.event.onResolutionStarted.bind(this::onResolutionStarted)
        e.event.onResolutionOver.bind(this::onResolutionOver)
        e.event.onNewQuery.bind(this::onNewQuery)
        e.event.onQueryOver.bind(this::onQueryOver)
        e.event.onNewSolution.bind(this::onNewSolution)
        e.event.onStdoutPrinted.bind(this::onStdoutPrinted)
        e.event.onStderrPrinted.bind(this::onStderrPrinted)
        e.event.onWarning.bind(this::onWarning)
        e.event.onNewSolver.bind(this::onNewSolver)
        e.event.onNewStaticKb.bind(this::onNewStaticKb)
        e.event.onSolveOptionsChanged.bind(this::onSolveOptionsChanged)
        e.event.onReset.bind(this::onReset)
        // TODO do this on page selection and on page solve option modification
//        updateTimeoutView(model.solveOptions.timeout)
        tabsFiles.tabs.add(PageView(e.event, model, this))
        handleSomeOpenFiles()
    }

    private fun onStart(e: Event<Unit>) {
        model.newPage()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun onUiThread(noinline action: () -> Unit) = JavaFxRunner.ui(action)

    private fun onQuit(e: Event<Unit>) {
        exitProcess(0)
    }

    private fun onReset(e: SolverEvent<PageID>) {
        // currentFileTab?.let { model.setCurrentFile(it.wholeText) }
        lsvWarnings.items.clear()
        txaStdout.clear()
        txaStderr.clear()
        lsvSolutions.items.clear()
    }

    private fun onTimeoutSliderMoved(value: Number) {
        model.currentPage?.timeout = round(10.0.pow(value.toDouble())).toLong()
    }

    private fun onSolveOptionsChanged(e: Event<SolveOptions>) {
        updateTimeoutView(e.event.timeout)
    }

    private fun updateTimeoutView(timeout: TimeDuration) {
        val duration = Duration.ofMillis(timeout)
        lblTimeout.text = duration.pretty()
    }

    @Suppress("Since15")
    private fun Duration.pretty(): String {
        val milliseconds = toMillisPart()
        val seconds = toSecondsPart()
        val minutes = toMinutesPart()
        val hours = toHoursPart()
        val days = toDaysPart() % 365
        val years = toDaysPart() / 365
        return sequenceOf(
            years.pretty("y"),
            days.pretty("d"),
            hours.pretty("h"),
            minutes.pretty("m"),
            seconds.pretty("s"),
            milliseconds.pretty("ms")
        ).filterNotNull().joinToString()
    }

    private fun Long.pretty(unit: String): String? =
        if (this == 0L) null else "$this$unit"

    private fun Int.pretty(unit: String): String? = toLong().pretty(unit)

    private fun onNewSolver(e: SolverEvent<PageID>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onNewStaticKb(e: SolverEvent<PageID>) = onUiThread {
        updateContextSensitiveView(e)
    }

    private fun onFileLoaded(e: Event<Page>) = onUiThread {
        // does nothing
    }

    private fun onPageClosed(e: Event<Page>) = onUiThread {
        fileTabs.firstOrNull { it.pageID == e.event.id }?.let {
            tabsFiles.tabs -= it
            handleNoMoreOpenFiles()
        }
    }

    private fun onFileSelected(e: Event<Page>) = onUiThread {
        fileTabs.firstOrNull { it.pageID == e.event.id }?.let {
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

    private fun onStdoutPrinted(e: Event<String>) = onUiThread {
        txaStdout.text += e.event
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
        if (isNonEmpty) {
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

    private fun onStderrPrinted(e: Event<String>) = onUiThread {
        txaStderr.text += e.event
        tabStderr.showNotification()
    }

    private fun onWarning(e: Event<Warning>) = onUiThread {
        lsvWarnings.items.add(e.event)
        lsvWarnings.scrollTo(e.event)
        tabWarnings.showNotification()
    }

    private fun onError(e: Event<Pair<Page, Throwable>>) = onUiThread {
        val dialog = Alert(Alert.AlertType.ERROR)
        val (_, exception: Throwable) = e.event
        dialog.title = exception::class.java.simpleName
        when (exception) {
            is SyntaxException.InTheorySyntaxError -> {
                dialog.headerText = "Syntax error in ${exception.page.name}"
                dialog.dialogPane.content = exception.message.toMonospacedText()
            }
            is SyntaxException.InQuerySyntaxError -> {
                dialog.headerText = "Syntax error in query"
                dialog.dialogPane.content = exception.message.toMonospacedText()
            }
            else -> {
                dialog.headerText = "Error"
                dialog.dialogPane.content = exception.message?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }?.toMonospacedText()
            }
        }
        dialog.dialogPane.minHeight = Region.USE_PREF_SIZE
        dialog.dialogPane.minWidth = Region.USE_PREF_SIZE
        dialog.showAndWait()
    }

    private fun String?.toMonospacedText(): Node {
        if (this == null) return Text()
        return Text(this).also { it.style = "-fx-font-family: monospaced" }
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
        model.currentPage?.query = txfQuery.text
    }

    @FXML
    fun onNextButtonPressed(e: ActionEvent) {
        if (model.currentPage?.state == Page.Status.IDLE) {
            startNewResolution()
        } else {
            continueResolution()
        }
    }

    @FXML
    fun onStdinChanged(e: KeyEvent) {
        model.currentPage?.stdin = txaStdin.text
    }

    @FXML
    fun onNextAllButtonPressed(e: ActionEvent) {
        if (model.currentPage?.state == Page.Status.IDLE) {
            startNewResolution(true)
        } else {
            continueResolution(true)
        }
    }

    @FXML
    fun onStopButtonPressed(e: ActionEvent) {
        model.currentPage?.stop()
    }

    @FXML
    fun onResetButtonPressed(e: ActionEvent) {
        model.currentPage?.reset()
    }

    private fun continueResolution(all: Boolean = false) {
        model.currentPage?.solve(if (all) Int.MAX_VALUE else 1)
    }

    private fun startNewResolution(all: Boolean = false) {
        model.currentPage?.next(if (all) Int.MAX_VALUE else 1)
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
    fun onTabSelectionChanged(e: JavaFxEvent) {
        val tab = e.source as Tab
        if (tab.isSelected) {
            tab.hideNotification()
        }
    }

    @FXML
    fun onNewFilePressed(e: ActionEvent) {
        model.newPage()
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
        model.load(JvmFile(file))
    }

    @FXML
    fun onCloseFilePressed(e: ActionEvent) {
        model.currentPage?.close()
    }

    private fun onFileSaved(e: Event<Pair<PageID, KtFile>>) {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Save file"
        alert.headerText = "File correctly saved"
        alert.contentText = e.event.first.name
        alert.dialogPane.minHeight = Region.USE_PREF_SIZE
        alert.showAndWait()
    }

    @FXML
    fun onSaveFilePressed(e: ActionEvent) {
        model.currentPage?.let { page ->
            when (val name = page.id) {
                is FileName -> page.save(name.file)
                else -> onSaveFileAsPressed(e)
            }
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
        model.currentPage?.save(JvmFile(file))
    }

    @FXML
    fun onReloadFilePressed(e: ActionEvent) {
        model.currentPage?.let { page ->
            (page.id as? FileName)?.let { model.load(it.file) }
        }
    }

    @FXML
    fun onUndoPressed(e: ActionEvent) {
        currentFileTab?.codeArea?.undo()
    }

    @FXML
    fun onRedoPressed(e: ActionEvent) {
        currentFileTab?.codeArea?.redo()
    }

    @FXML
    fun onCutPressed(e: ActionEvent) {
        currentFileTab?.codeArea?.cut()
    }

    @FXML
    fun onCopyPressed(e: ActionEvent) {
        currentFileTab?.codeArea?.copy()
    }

    @FXML
    fun onPastePressed(e: ActionEvent) {
        currentFileTab?.codeArea?.paste()
    }

    @FXML
    fun onDeletePressed(e: ActionEvent) {
        currentFileTab?.let {
            it.codeArea.deleteText(it.codeArea.selection)
        }
    }

    @FXML
    fun onSelectAllPressed(e: ActionEvent) {
        currentFileTab?.codeArea?.selectAll()
    }

    @FXML
    fun onUnselectAllPressed(e: ActionEvent) {
        currentFileTab?.codeArea?.deselect()
    }

    @FXML
    fun onSettingsPressed(e: ActionEvent) {
    }

    @FXML
    fun onAbout(e: ActionEvent) {
        this.onAbout()
    }

    fun customizeModel(setup: ModelConfigurator) = setup(model)

    /**
     * Adds a [Tab] to the UI. If a [Tab] with the same [Tab.id] as [tab] is
     * already present, it gets substituted.
     */
    fun addTab(tab: Tab) {
        val index = this.tabsStreams.tabs.indexOfFirst { it.id == tab.id }
        if (index >= 0) {
            this.tabsStreams.tabs[index] = tab
        } else {
            this.tabsStreams.tabs.add(tab)
        }
    }

    fun setOnClose(onClose: () -> Unit) {
        this.onClose = onClose
    }

    fun setOnAbout(onAbout: () -> Unit) {
        this.onAbout = onAbout
    }
}
