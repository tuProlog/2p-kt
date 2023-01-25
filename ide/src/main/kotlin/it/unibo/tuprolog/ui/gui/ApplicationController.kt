package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.utils.io.JvmFile
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.Slider
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.time.Duration
import java.util.Locale
import java.util.ResourceBundle
import kotlin.math.pow
import kotlin.math.round
import kotlin.system.exitProcess
import javafx.event.Event as JavaFxEvent

@Suppress("UNUSED_PARAMETER", "unused")
class ApplicationController(
    private val model: Application
) : AbstractController() {

    private var onClose = {}

    private var onAbout = {}

    @FXML
    private lateinit var tabsFiles: TabPane

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


    private val stage: Stage get() = root.scene.window as Stage

    private val fileTabs: Sequence<PageView>
        get() = tabsFiles.tabs.asSequence().filterIsInstance<PageView>()

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
        model.onPageSelected.bind(this::onPageSelected)
        model.onPageUnselected.bind(this::onPageUnselected)

        model.onQuit.bind(this::onQuit)

        sldTimeout.valueProperty().addListener { _, _, value -> onTimeoutSliderMoved(value) }
    }

    fun notifyCaretPosition(position: Pair<Int, Int>?) {
        val (l, c) = position ?: ("-" to "-")
        lblCaret.text = "Line: $l | Column: $c"
    }

    fun notifyStatus(status: String) {
        lblStatus.text = status
    }

    private fun onPageCreated(e: Event<Page>) = onUiThread {
        tabsFiles.tabs.add(PageView(e.event, this))
        handleSomeOpenFiles()
    }

    private fun onStart(e: Event<Unit>) {
        model.newPage()
    }

    private fun onQuit(e: Event<Unit>) {
        exitProcess(0)
    }

    private fun onTimeoutSliderMoved(value: Number) {
        model.currentPage?.timeout = round(10.0.pow(value.toDouble())).toLong()
    }

    fun notifyTimeoutUpdate(timeout: TimeDuration) {
        val duration = Duration.ofMillis(timeout)
        lblTimeout.text = duration.pretty()
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

    private fun onPageSelected(e: Event<Page>) = onUiThread {
        fileTabs.firstOrNull { it.pageID == e.event.id }?.let {
            it.updateSyntaxColoring()
            tabsFiles.selectionModel.select(it)
        }
    }

    private fun onPageUnselected(e: Event<Page>) = onUiThread {
        tabsFiles.selectionModel.clearSelection()
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

    @FXML
    fun onQuitRequested(e: ActionEvent) {
        this.onClose()
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
}
