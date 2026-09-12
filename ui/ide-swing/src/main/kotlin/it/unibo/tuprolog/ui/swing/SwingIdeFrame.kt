package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.ui.gui.controller.ApplicationAction
import it.unibo.tuprolog.ui.gui.controller.ConsumptionMode
import it.unibo.tuprolog.ui.gui.controller.DocumentAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.resolve
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.formatDurationInput
import it.unibo.tuprolog.ui.gui.presentation.parseDurationInput
import it.unibo.tuprolog.ui.gui.template.TheoryTemplate
import kotlinx.coroutines.CoroutineScope
import org.fife.ui.rsyntaxtextarea.ErrorStrip
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Taskbar
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.net.URI
import javax.swing.AbstractAction
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JRadioButtonMenuItem
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.WindowConstants
import javax.swing.event.CaretEvent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.DefaultEditorKit
import kotlin.math.max

/**
 * Swing view/adapter. It never creates or mutates solver objects: all behaviour goes through [GuiController].
 * The fixed query row is rebound atomically to the selected page and therefore remains semantically page-specific.
 *
 * This is the single Swing binding for the whole IDE window: one menu bar, one query row, and every inspector
 * tab, each wired to a handful of fields and a handful of listener callbacks. Splitting that wiring across
 * several classes would trade a class that is large but easy to follow top-to-bottom for several smaller classes
 * passing the same dozen fields back and forth, which is not a real improvement -- hence the suppression below.
 */
@Suppress("LargeClass", "TooManyFunctions", "LongParameterList")
class SwingIdeFrame(
    private val controller: GuiController,
    private val scope: CoroutineScope,
    private val featureRenderers: SwingFeatureRendererRegistry = SwingFeatureRendererRegistry(),
    private val templates: List<TheoryTemplate> = emptyList(),
    initialFontSize: Int = 14,
    /**
     * Per-page editor zoom levels to restore, positional (matching the order pages are (re-)created in, e.g.
     * by [restoreWorkspace] from persisted state) rather than keyed by [PageId] - which doesn't exist yet for
     * a page still to be created. Consulted once, the first time each page's editor is built.
     */
    private val initialPageFontSizes: List<Int> = emptyList(),
    /** Deletes the persisted workspace file and suppresses the next normal-shutdown autosave (see "Delete
     * Persisted State" in the Settings menu); a no-op host (e.g. a test) can safely leave this at its default. */
    private val onDeletePersistedState: () -> Unit = {},
) : JFrame() {
    /** The most recently applied editor font size; read back by the host when persisting the session. */
    internal var currentFontSize: Int = initialFontSize
        private set

    /** Each page's own current editor zoom level, for [it.unibo.tuprolog.ui.swing.capturePersistedWorkspace]. */
    internal val pageFontSizes = mutableMapOf<PageId, Int>()
    private val editorTabs = JTabbedPane().apply { name = "editorTabs" }
    private val lowerTabs = JTabbedPane().apply { name = "lowerTabs" }
    private val queryField = PrologQueryField().apply { name = "queryField" }
    private val solveButton = JButton("Solve", Icons.SOLVE).apply { name = "solveButton" }
    private val solve10Button = JButton("Solve 10", Icons.SOLVE_10).apply { name = "solve10Button" }
    private val solve100Button = JButton("Solve 100", Icons.SOLVE_100).apply { name = "solve100Button" }
    private val solveAllButton = JButton("Solve all", Icons.SOLVE_ALL).apply { name = "solveAllButton" }
    private val stopButton = JButton("Stop", Icons.STOP).apply { name = "stopButton" }
    private val resetButton = JButton("Reset", Icons.RESET).apply { name = "resetButton" }
    private val timeoutField = JTextField("5s", TIMEOUT_FIELD_COLUMNS).apply { name = "timeoutField" }
    private val statusLabel = JLabel("Idle").apply { name = "statusLabel" }
    private val caretLabel = JLabel("Line 0, column 0", SwingConstants.RIGHT).apply { name = "caretLabel" }

    private val solutionsTree = SolutionTree().apply { name = "solutionsTree" }
    private val clearSolutionsButton = JButton("Clear solutions", Icons.CLEAR).apply { name = "clearSolutionsButton" }
    private val stdinArea = editorArea().apply { name = "stdinArea" }
    private val stdoutArea = readOnlyArea().apply { name = "stdoutArea" }
    private val stderrArea = readOnlyArea().apply { name = "stderrArea" }
    private val warningsArea = readOnlyArea().apply { name = "warningsArea" }
    private val diagnosticsList = DiagnosticsList().apply { name = "diagnosticsList" }
    private val operatorsTable = OperatorsTable().apply { name = "operatorsTable" }
    private val flagsTable = FlagsTable().apply { name = "flagsTable" }
    private val librariesTree = LibrariesTree().apply { name = "librariesTree" }
    private val staticKbArea =
        PrologEditor(currentFontSize).apply {
            isEditable = false
            setHighlightCurrentLine(false)
            name = "staticKbArea"
        }
    private val dynamicKbArea =
        PrologEditor(currentFontSize).apply {
            isEditable = false
            setHighlightCurrentLine(false)
            name = "dynamicKbArea"
        }

    private val pageEditors = linkedMapOf<PageId, PrologEditor>()
    private val pageComponents = linkedMapOf<PageId, JComponent>()
    private val lowerPanelIds = mutableMapOf<Int, PanelId>()
    private val lowerPanelTitles = mutableMapOf<Int, String>()
    private val lastSeenStaticKb = mutableMapOf<PageId, String>()
    private val lastSeenDynamicKb = mutableMapOf<PageId, String>()
    private val extensionComponents = mutableMapOf<it.unibo.tuprolog.ui.gui.identity.FeatureId, JComponent>()
    private val extensionTabIndices = mutableMapOf<it.unibo.tuprolog.ui.gui.identity.FeatureId, Int>()
    private val featureContext = SwingFeatureContext(controller, scope)
    private val searchActions by lazy { PrologSearchActions(this, ::selectedEditor) }

    private var renderedState: GuiState? = null
    private var queryBoundPageId: PageId? = null
    private var stdinBoundPageId: PageId? = null
    private var rendering = false
    private var queryHistoryIndex: Int? = null
    private var queryHistoryDraft = ""
    private var browsingQueryHistory = false

    init {
        title = "tuProlog IDE v${Info.VERSION}"
        iconImage =
            java.awt.Toolkit
                .getDefaultToolkit()
                .getImage(javaClass.getResource("/logo.png"))
        // The window icon above only covers the title bar/taskbar entry; the OS-level app icon (macOS Dock,
        // GNOME/KDE app switcher, ...) is a separate concept that needs the Taskbar API, where supported.
        if (Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) taskbar.iconImage = iconImage
        }
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)
        preferredSize = Dimension(PREFERRED_WINDOW_WIDTH, PREFERRED_WINDOW_HEIGHT)
        layout = BorderLayout()
        jMenuBar = createMenuBar()

        val vertical =
            JSplitPane(JSplitPane.VERTICAL_SPLIT, editorTabs, createLowerPanel()).apply {
                resizeWeight = WORKSPACE_SPLIT_RESIZE_WEIGHT
                dividerLocation = WORKSPACE_SPLIT_DIVIDER_LOCATION
            }
        add(vertical, BorderLayout.CENTER)
        add(createStatusBar(), BorderLayout.SOUTH)

        editorTabs.addChangeListener { onEditorTabChanged() }
        lowerTabs.addChangeListener { onLowerTabChanged() }
        installQueryListeners()
        installStdinListener()
        installWindowListener()
        solutionsTree.onQuerySelected =
            { query -> queryBoundPageId?.let { dispatch(PageAction.ChangeQuery(it, query)) } }
        diagnosticsList.onDiagnosticSelected = { diagnostic -> navigateToDiagnostic(diagnostic) }
        flagsTable.onFlagChanged = { name, value -> changeFlag(name, value) }
        operatorsTable.onOperatorAdded = ::addOperator
        pack()
        setLocationRelativeTo(null)
    }

    fun render(state: GuiState) {
        check(javax.swing.SwingUtilities.isEventDispatchThread()) { "Swing rendering must occur on the EDT" }
        rendering = true
        try {
            renderedState = state
            syncEditorTabs(state)
            renderSelectedPage(state)
            title = "tuProlog IDE v${Info.VERSION}"
        } finally {
            rendering = false
        }
    }

    private fun createLowerPanel(): JComponent {
        val controlHeight = maxOf(timeoutField.preferredSize.height, solveButton.preferredSize.height)
        timeoutField.preferredSize = Dimension(TIMEOUT_FIELD_WIDTH, controlHeight)
        queryField.preferredSize = Dimension(queryField.preferredSize.width, controlHeight)
        listOf(solveButton, solve10Button, solve100Button, solveAllButton, stopButton, resetButton).forEach {
            // Only the height is forced (for row alignment); the width is left to Swing's own icon+caption
            // measurement, since a fixed width narrower than that would silently clip the button's caption.
            it.preferredSize = Dimension(it.preferredSize.width, controlHeight)
        }
        val queryRow =
            JPanel(BorderLayout(QUERY_ROW_HGAP, 0)).apply {
                border =
                    BorderFactory.createEmptyBorder(
                        QUERY_ROW_BORDER_INSET,
                        QUERY_ROW_BORDER_INSET,
                        QUERY_ROW_BORDER_INSET,
                        QUERY_ROW_BORDER_INSET,
                    )
                add(JLabel("?-"), BorderLayout.WEST)
                add(queryField, BorderLayout.CENTER)
                add(
                    JPanel(FlowLayout(FlowLayout.RIGHT, QUERY_BUTTONS_HGAP, 0)).apply {
                        add(solveButton)
                        add(solve10Button)
                        add(solve100Button)
                        add(solveAllButton)
                        add(stopButton)
                        add(resetButton)
                        add(JLabel("Timeout"))
                        add(timeoutField)
                    },
                    BorderLayout.EAST,
                )
            }

        addSolutionsTab()
        // Extension tabs (e.g. ide-plp-swing's BDD) are registered right after Solutions, since they present
        // solution-derived content too, ahead of the generic Stdin..Dynamic KB tabs every profile has.
        for (renderer in featureRenderers.all()) {
            val component = renderer.createComponent(featureContext)
            extensionComponents[renderer.featureId] = component
            extensionTabIndices[renderer.featureId] = lowerTabs.tabCount
            lowerTabs.addTab(renderer.displayName, component)
        }
        addLowerTab("Stdin", PanelId.STDIN, stdinArea, Icons.STDIN)
        addLowerTab("Stdout", PanelId.STDOUT, stdoutArea, Icons.STDOUT)
        addLowerTab("Stderr", PanelId.STDERR, stderrArea, Icons.STDERR)
        addLowerTab("Warnings", PanelId.WARNINGS, warningsArea, Icons.WARNINGS)
        addLowerTab("Diagnostics", PanelId.DIAGNOSTICS, diagnosticsList, Icons.DIAGNOSTICS)
        addLowerTab("Operators", PanelId.OPERATORS, operatorsTable, Icons.OPERATORS)
        addLowerTab("Flags", PanelId.FLAGS, flagsTable, Icons.FLAGS)
        addLowerTab("Libraries", PanelId.LIBRARIES, librariesTree, Icons.LIBRARIES)
        addLowerTab("Static KB", PanelId.STATIC_KB, staticKbArea, Icons.STATIC_KB)
        addLowerTab("Dynamic KB", PanelId.DYNAMIC_KB, dynamicKbArea, Icons.DYNAMIC_KB)

        return JPanel(BorderLayout()).apply {
            add(queryRow, BorderLayout.NORTH)
            add(lowerTabs, BorderLayout.CENTER)
        }
    }

    private fun addSolutionsTab() {
        val index = lowerTabs.tabCount
        clearSolutionsButton.toolTipText = "Remove concluded queries from this list; a query still running is kept"
        clearSolutionsButton.addActionListener {
            selectedPage()?.let { page -> dispatch(PageAction.ClearHistory(page.id)) }
        }
        val panel =
            JPanel(BorderLayout()).apply {
                add(
                    JPanel(FlowLayout(FlowLayout.LEFT, SOLUTIONS_TOOLBAR_HGAP, 2)).apply { add(clearSolutionsButton) },
                    BorderLayout.NORTH,
                )
                add(JScrollPane(solutionsTree), BorderLayout.CENTER)
            }
        lowerTabs.addTab("Solutions", Icons.SOLUTIONS, panel)
        lowerPanelIds[index] = PanelId.SOLUTIONS
        lowerPanelTitles[index] = "Solutions"
    }

    private fun addLowerTab(
        title: String,
        panelId: PanelId,
        component: JComponent,
        icon: Icon? = null,
    ) {
        val index = lowerTabs.tabCount
        lowerTabs.addTab(
            title,
            icon,
            if (component is PrologEditor) RTextScrollPane(component, true) else JScrollPane(component),
        )
        lowerPanelIds[index] = panelId
        lowerPanelTitles[index] = title
    }

    private fun createStatusBar(): JComponent =
        JPanel(BorderLayout()).apply {
            border =
                BorderFactory.createEmptyBorder(
                    STATUS_BAR_VERTICAL_INSET,
                    STATUS_BAR_HORIZONTAL_INSET,
                    STATUS_BAR_VERTICAL_INSET,
                    STATUS_BAR_HORIZONTAL_INSET,
                )
            add(statusLabel, BorderLayout.CENTER)
            add(caretLabel, BorderLayout.EAST)
        }

    private fun createMenuBar(): JMenuBar =
        JMenuBar().apply {
            name = "menuBar"
            add(fileMenu())
            add(editMenu())
            add(searchMenu())
            add(lookAndFeelMenu())
            add(settingsMenu())
            add(helpMenu())
        }

    private fun fileMenu(): JMenu =
        JMenu("File").apply {
            name = "fileMenu"
            mnemonic = KeyEvent.VK_F
            addNewAndOpenItems(this)
            addSeparator()
            addSaveAndCloseItems(this)
            add(menuItem("Reload", null, "reloadMenuItem", Icons.RELOAD) { reloadSelected() })
            add(
                menuItem("File properties", null, "filePropertiesMenuItem", Icons.FILE_PROPERTIES) {
                    showFileProperties()
                },
            )
            addSeparator()
            add(
                menuItem(
                    "Quit",
                    KeyStroke.getKeyStroke(KeyEvent.VK_Q, menuMask()),
                    "quitMenuItem",
                    Icons.QUIT,
                ) { requestExit() },
            )
        }

    private fun addNewAndOpenItems(menu: JMenu) {
        menu.add(
            menuItem(
                "New",
                KeyStroke.getKeyStroke(KeyEvent.VK_N, menuMask()),
                "newMenuItem",
                Icons.NEW,
            ) { newDocument() },
        )
        menu.add(menuItem("New scratch page", null, "newScratchPageMenuItem", Icons.NEW) { newScratchPage() })
        if (templates.isNotEmpty()) menu.add(newFromTemplateMenu())
        menu.add(
            menuItem(
                "Open…",
                KeyStroke.getKeyStroke(KeyEvent.VK_O, menuMask()),
                "openMenuItem",
                Icons.OPEN,
            ) { openDocument() },
        )
    }

    private fun addSaveAndCloseItems(menu: JMenu) {
        menu.add(
            menuItem(
                "Close page",
                KeyStroke.getKeyStroke(KeyEvent.VK_W, menuMask()),
                "closePageMenuItem",
                Icons.CLOSE_PAGE,
            ) { closeSelectedPage() },
        )
        menu.add(
            menuItem(
                "Save",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask()),
                "saveMenuItem",
                Icons.SAVE,
            ) { saveSelected(false) },
        )
        menu.add(
            menuItem(
                "Save as…",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask() or InputEvent.SHIFT_DOWN_MASK),
                "saveAsMenuItem",
                Icons.SAVE_AS,
            ) { saveSelected(true) },
        )
    }

    private fun editMenu(): JMenu =
        JMenu("Edit").apply {
            name = "editMenu"
            mnemonic = KeyEvent.VK_E
            add(
                JMenuItem(DefaultEditorKit.CutAction()).apply {
                    text = "Cut"
                    name = "cutMenuItem"
                    icon = Icons.CUT
                },
            )
            add(
                JMenuItem(DefaultEditorKit.CopyAction()).apply {
                    text = "Copy"
                    name = "copyMenuItem"
                    icon = Icons.COPY
                },
            )
            add(
                JMenuItem(DefaultEditorKit.PasteAction()).apply {
                    text = "Paste"
                    name = "pasteMenuItem"
                    icon = Icons.PASTE
                },
            )
            add(
                JMenuItem(SelectAllAction()).apply {
                    text = "Select all"
                    name = "selectAllMenuItem"
                    accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_A, menuMask())
                    icon = Icons.SELECT_ALL
                },
            )
        }

    private fun searchMenu(): JMenu =
        JMenu("Search").apply {
            name = "searchMenu"
            mnemonic = KeyEvent.VK_S
            add(
                menuItem(
                    "Find…",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F, menuMask()),
                    "findMenuItem",
                    Icons.FIND,
                ) { searchActions.showFind() },
            )
            add(
                menuItem(
                    "Replace…",
                    KeyStroke.getKeyStroke(KeyEvent.VK_H, menuMask()),
                    "replaceMenuItem",
                    Icons.REPLACE,
                ) { searchActions.showReplace() },
            )
            add(
                menuItem(
                    "Go to line…",
                    KeyStroke.getKeyStroke(KeyEvent.VK_G, menuMask()),
                    "goToLineMenuItem",
                    Icons.GO_TO_LINE,
                ) { searchActions.showGoToLine() },
            )
        }

    private fun lookAndFeelMenu(): JMenu =
        JMenu("Look and Feel").apply {
            name = "lookAndFeelMenu"
            val group = ButtonGroup()
            val currentName = UIManager.getLookAndFeel()?.name
            for (info in installedLookAndFeels()) {
                val item =
                    JRadioButtonMenuItem(info.name, info.name == currentName).apply {
                        name = "lookAndFeelMenuItem.${info.name}"
                        addActionListener { applyLookAndFeel(info.name, listOf(this@SwingIdeFrame)) }
                    }
                group.add(item)
                add(item)
            }
        }

    private fun settingsMenu(): JMenu =
        JMenu("Settings").apply {
            name = "settingsMenu"
            add(
                menuItem("Restore default settings", null, "restoreDefaultSettingsMenuItem") {
                    resetToDefaultSettings()
                },
            )
            add(
                menuItem("Delete persisted state…", null, "deletePersistedStateMenuItem") {
                    confirmAndDeletePersistedState()
                },
            )
        }

    /** Resets editor zoom (every open page) and the look-and-feel to their hard-coded defaults. */
    private fun resetToDefaultSettings() {
        currentFontSize = DEFAULT_FONT_SIZE
        pageEditors.forEach { (pageId, editor) ->
            editor.font = editor.font.deriveFont(DEFAULT_FONT_SIZE.toFloat())
            pageFontSizes[pageId] = DEFAULT_FONT_SIZE
        }
        applyLookAndFeel(DEFAULT_LOOK_AND_FEEL, listOf(this))
    }

    private fun confirmAndDeletePersistedState() {
        val selected =
            JOptionPane.showConfirmDialog(
                this,
                "Delete the saved workspace? Nothing will be restored the next time the app starts; " +
                    "documents currently open are not affected.",
                "Delete persisted state",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
            )
        if (selected == JOptionPane.OK_OPTION) onDeletePersistedState()
    }

    private fun helpMenu(): JMenu =
        JMenu("Help").apply {
            name = "helpMenu"
            add(menuItem("About", null, "aboutMenuItem", Icons.ABOUT) { showAbout() })
            add(
                menuItem("Report an issue…", null, "reportIssueMenuItem", Icons.REPORT_ISSUE) {
                    showReportIssueDialog()
                },
            )
        }

    private fun newFromTemplateMenu(): JMenu =
        JMenu("New from template").apply {
            name = "newFromTemplateMenu"
            icon = Icons.NEW_FROM_TEMPLATE
            for (template in templates) {
                add(
                    JMenuItem(
                        object : AbstractAction(template.displayName) {
                            override fun actionPerformed(event: java.awt.event.ActionEvent?) {
                                dispatch(WorkspaceAction.NewDocumentPage("${template.id}.pl", template.source))
                            }
                        },
                    ).apply {
                        name = "newFromTemplateMenuItem.${template.id}"
                        toolTipText = template.description.ifBlank { null }
                    },
                )
            }
        }

    private fun menuItem(
        label: String,
        accelerator: KeyStroke?,
        name: String,
        icon: Icon? = null,
        action: () -> Unit,
    ): JMenuItem =
        JMenuItem(
            object : AbstractAction(label) {
                override fun actionPerformed(event: java.awt.event.ActionEvent?) = action()
            },
        ).apply {
            this.accelerator = accelerator
            this.name = name
            this.icon = icon
        }

    private fun installQueryListeners() {
        queryField.document.addDocumentListener(
            object : DocumentListener {
                override fun insertUpdate(event: DocumentEvent) = queryChanged()

                override fun removeUpdate(event: DocumentEvent) = queryChanged()

                override fun changedUpdate(event: DocumentEvent) = queryChanged()
            },
        )
        queryField.onSubmit = { solve(ConsumptionMode.ONE) }
        timeoutField.inputVerifier =
            object : javax.swing.InputVerifier() {
                override fun verify(input: JComponent): Boolean =
                    parseDurationInput(timeoutField.text)?.let { true } ?: showInvalidTimeout()
            }
        timeoutField.addActionListener { commitTimeout() }
        timeoutField.addFocusListener(
            object : java.awt.event.FocusAdapter() {
                override fun focusLost(event: java.awt.event.FocusEvent?) = commitTimeout()
            },
        )
        queryField.addKeyListener(
            object : KeyAdapter() {
                override fun keyPressed(event: KeyEvent) {
                    when (event.keyCode) {
                        KeyEvent.VK_UP -> navigateQueryHistory(-1)
                        KeyEvent.VK_DOWN -> navigateQueryHistory(1)
                        else -> return
                    }
                    event.consume()
                }
            },
        )
        solveButton.addActionListener {
            val page = selectedPage() ?: return@addActionListener
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) {
                dispatch(PageAction.Next(page.id, ConsumptionMode.ONE))
            } else {
                dispatch(PageAction.Solve(page.id, ConsumptionMode.ONE))
            }
        }
        solve10Button.addActionListener { solve(ConsumptionMode.TEN) }
        solve100Button.addActionListener { solve(ConsumptionMode.HUNDRED) }
        solveAllButton.addActionListener {
            val page = selectedPage() ?: return@addActionListener
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) {
                dispatch(PageAction.Next(page.id, ConsumptionMode.ALL))
            } else {
                dispatch(PageAction.Solve(page.id, ConsumptionMode.ALL))
            }
        }
        stopButton.addActionListener { selectedPage()?.let { dispatch(PageAction.Stop(it.id)) } }
        resetButton.addActionListener { selectedPage()?.let { dispatch(PageAction.Reset(it.id)) } }
    }

    private fun installStdinListener() {
        stdinArea.document.addDocumentListener(
            object : DocumentListener {
                override fun insertUpdate(event: DocumentEvent) = stdinChanged()

                override fun removeUpdate(event: DocumentEvent) = stdinChanged()

                override fun changedUpdate(event: DocumentEvent) = stdinChanged()
            },
        )
    }

    private fun installWindowListener() {
        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(event: WindowEvent?) = requestExit()
            },
        )
    }

    private fun syncEditorTabs(state: GuiState) {
        val pages = state.workspace.pages
        val existing = pageComponents.keys.toSet()
        val wanted = pages.map { it.id }.toSet()
        for (removed in existing - wanted) {
            val component = pageComponents.remove(removed)
            pageEditors.remove(removed)
            lastSeenStaticKb.remove(removed)
            lastSeenDynamicKb.remove(removed)
            if (component != null) editorTabs.remove(component)
        }

        pages.forEachIndexed { index, page ->
            val component = pageComponents.getOrPut(page.id) { createEditorComponent(page, index) }
            lastSeenStaticKb.getOrPut(page.id) { "" }
            lastSeenDynamicKb.getOrPut(page.id) { "" }
            val currentIndex = editorTabs.indexOfComponent(component)
            if (currentIndex < 0) {
                editorTabs.insertTab(pageTitle(page, state), null, component, null, index)
            } else if (currentIndex != index) {
                editorTabs.remove(component)
                editorTabs.insertTab(pageTitle(page, state), null, component, null, index)
            }
            editorTabs.setTitleAt(index, pageTitle(page, state))
            val area = pageEditors.getValue(page.id)
            val source = sourceText(page, state)
            if (area.text != source) {
                area.text = source
                area.caretPosition = area.document.length.coerceAtMost(area.caretPosition)
            }
            area.highlight(page.solverSession.inspection.operators)
        }

        val selectedIndex = pages.indexOfFirst { it.id == state.workspace.selectedPageId }
        if (selectedIndex >= 0 && editorTabs.selectedIndex != selectedIndex) {
            editorTabs.selectedIndex = selectedIndex
        }
    }

    private fun createEditorComponent(
        page: PageState,
        index: Int,
    ): JComponent {
        val initialSize = initialPageFontSizes.getOrNull(index) ?: currentFontSize
        val area =
            PrologEditor(initialSize).apply {
                name = "pageEditor"
                document.addDocumentListener(
                    object : DocumentListener {
                        override fun insertUpdate(event: DocumentEvent) = editorChanged(page.id)

                        override fun removeUpdate(event: DocumentEvent) = editorChanged(page.id)

                        override fun changedUpdate(event: DocumentEvent) = editorChanged(page.id)
                    },
                )
                addCaretListener { event -> caretChanged(event) }
                onZoomChanged = { size ->
                    currentFontSize = size
                    pageFontSizes[page.id] = size
                }
            }
        pageFontSizes[page.id] = initialSize
        pageEditors[page.id] = area
        return JPanel(BorderLayout()).apply {
            add(RTextScrollPane(area, true), BorderLayout.CENTER)
            add(ErrorStrip(area), BorderLayout.EAST)
        }
    }

    private fun renderSelectedPage(state: GuiState) {
        val page = state.workspace.selectedPageId?.let(state.workspace::page)
        queryBoundPageId = page?.id
        stdinBoundPageId = page?.id
        queryField.isEnabled = page != null
        stdinArea.isEnabled = page != null && page.resolution.status != ResolutionStatus.RUNNING

        if (page == null) {
            renderNoPage()
            return
        }

        renderQueryAndStdin(state, page)
        renderSolutionsAndConsoles(page)
        renderInspectors(page)
        renderSolveControls(page)
        statusLabel.text = statusText(page)
        updateLowerTabTitles(page)
        acknowledgeVisiblePanel(page)
        renderExtensionFeatures(page)
    }

    private fun renderNoPage() {
        queryField.text = ""
        clearLowerAreas()
        statusLabel.text = "No page"
        solveButton.isEnabled = false
        solve10Button.isEnabled = false
        solve100Button.isEnabled = false
        solveAllButton.isEnabled = false
        stopButton.isEnabled = false
        resetButton.isEnabled = false
    }

    private fun renderQueryAndStdin(
        state: GuiState,
        page: PageState,
    ) {
        if (queryField.text != page.query.text) queryField.text = page.query.text
        queryField.highlight(page.solverSession.inspection.operators)
        if (stdinArea.text != page.console.stdin) stdinArea.text = page.console.stdin
        val effective = state.workspace.configuration.resolve(page.configuration)
        if (!timeoutField.isFocusOwner) timeoutField.text = formatDurationInput(effective.timeout)
    }

    private fun renderSolutionsAndConsoles(page: PageState) {
        val solutionEntries = solutionEntries(page)
        val focusedQuery =
            page.resolution.query
                ?.takeIf { query -> solutionEntries.any { it.query == query } }
                ?: solutionEntries.lastOrNull()?.query
        solutionsTree.render(solutionEntries, focusedQuery)
        stdoutArea.text = page.console.stdout.text
        stderrArea.text = page.console.stderr.text
        warningsArea.text =
            page.console.warnings.values.joinToString("\n\n") { warning ->
                buildString {
                    append(warning.message)
                    if (warning.logicStackTrace.isNotEmpty()) {
                        append(
                            "\n",
                        ).append(warning.logicStackTrace.joinToString("\n"))
                    }
                }
            }
    }

    private fun renderInspectors(page: PageState) {
        diagnosticsList.render(pageEditors[page.id]?.diagnostics.orEmpty())
        operatorsTable.render(page.solverSession.inspection.operators)
        flagsTable.render(page.solverSession.inspection.flags)
        flagsTable.isEnabled = page.resolution.status != ResolutionStatus.RUNNING
        librariesTree.render(page.solverSession.inspection.libraries)
        staticKbArea.text = page.solverSession.inspection.staticKnowledgeBase
        staticKbArea.highlight(page.solverSession.inspection.operators)
        dynamicKbArea.text = page.solverSession.inspection.dynamicKnowledgeBase
        dynamicKbArea.highlight(page.solverSession.inspection.operators)
    }

    private fun renderSolveControls(page: PageState) {
        val awaitingContinuation = page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION
        solveButton.text = if (awaitingContinuation) "Next" else "Solve"
        solve10Button.text = if (awaitingContinuation) "Next 10" else "Solve 10"
        solve100Button.text = if (awaitingContinuation) "Next 100" else "Solve 100"
        solveAllButton.text = if (awaitingContinuation) "All next" else "Solve all"
        val canSolveOrContinue = page.resolution.canSolve || page.resolution.canContinue
        solveButton.isEnabled = canSolveOrContinue
        solve10Button.isEnabled = canSolveOrContinue
        solve100Button.isEnabled = canSolveOrContinue
        solveAllButton.isEnabled = canSolveOrContinue
        stopButton.isEnabled = page.resolution.canStop
        resetButton.isEnabled = true
        timeoutField.isEnabled = page.resolution.status != ResolutionStatus.RUNNING
    }

    private fun renderExtensionFeatures(page: PageState) {
        for ((featureId, component) in extensionComponents) {
            val renderer = featureRenderers.renderer(featureId) ?: continue
            val featureState = page.features[featureId] ?: PageFeatureState()
            val isFeatureEnabled = page.solverSession.capabilities.containsAll(renderer.requiredCapabilities)
            extensionTabIndices[featureId]?.let { lowerTabs.setEnabledAt(it, isFeatureEnabled) }
            renderer.render(component, page, featureState)
        }
    }

    private fun updateLowerTabTitles(page: PageState) {
        for ((index, panelId) in lowerPanelIds) {
            val baseTitle = lowerPanelTitles.getValue(index)
            val unread =
                when (panelId) {
                    PanelId.SOLUTIONS -> page.resolution.hasUnreadChanges
                    PanelId.STDOUT -> page.console.stdout.hasUnreadChanges
                    PanelId.STDERR -> page.console.stderr.hasUnreadChanges
                    PanelId.WARNINGS -> page.console.warnings.hasUnreadChanges
                    PanelId.DIAGNOSTICS -> page.diagnostics.hasUnreadChanges
                    PanelId.STATIC_KB -> lastSeenStaticKb[page.id] != page.solverSession.inspection.staticKnowledgeBase
                    PanelId.DYNAMIC_KB ->
                        lastSeenDynamicKb[page.id] !=
                            page.solverSession.inspection.dynamicKnowledgeBase
                    else -> false
                }
            val title = if (unread) "$baseTitle*" else baseTitle
            if (lowerTabs.getTitleAt(index) != title) lowerTabs.setTitleAt(index, title)
        }
    }

    private fun acknowledgeVisiblePanel(page: PageState) {
        val panel = lowerPanelIds[lowerTabs.selectedIndex]
        if (panel == null) return
        when (panel) {
            PanelId.STATIC_KB -> lastSeenStaticKb[page.id] = page.solverSession.inspection.staticKnowledgeBase
            PanelId.DYNAMIC_KB -> lastSeenDynamicKb[page.id] = page.solverSession.inspection.dynamicKnowledgeBase
            else -> {
                val unread =
                    when (panel) {
                        PanelId.SOLUTIONS -> page.resolution.hasUnreadChanges
                        PanelId.STDOUT -> page.console.stdout.hasUnreadChanges
                        PanelId.STDERR -> page.console.stderr.hasUnreadChanges
                        PanelId.WARNINGS -> page.console.warnings.hasUnreadChanges
                        PanelId.DIAGNOSTICS -> page.diagnostics.hasUnreadChanges
                        else -> false
                    }
                if (unread) dispatch(PageAction.MarkPanelRead(page.id, panel))
            }
        }
    }

    private fun solutionEntries(page: PageState): List<SolutionQueryEntry> {
        val history =
            page.history.resolutions.map {
                SolutionQueryEntry(it.query, it.solutions, hasUnexploredPaths = false)
            }
        val current = page.resolution
        val currentQuery = current.query
        val live =
            if (currentQuery != null &&
                current.status in setOf(ResolutionStatus.RUNNING, ResolutionStatus.AWAITING_CONTINUATION)
            ) {
                listOf(SolutionQueryEntry(currentQuery, current.solutions, hasUnexploredPaths = true))
            } else {
                emptyList()
            }
        return history + live
    }

    private fun navigateToDiagnostic(diagnostic: Diagnostic) {
        val editor = selectedPage()?.let { pageEditors[it.id] }
        val offset =
            editor?.let {
                diagnostic.range
                    ?.start
                    ?.offset
                    ?.coerceIn(0, it.document.length)
            }
        if (editor == null || offset == null) return
        editor.caretPosition = offset
        editor.requestFocusInWindow()
        runCatching { editor.scrollRectToVisible(editor.modelToView2D(offset).bounds) }
    }

    private fun changeFlag(
        name: String,
        value: String,
    ) {
        val page = selectedPage() ?: return
        dispatch(
            PageAction.ChangeConfiguration(
                page.id,
                page.configuration.copy(optionOverrides = page.configuration.optionOverrides + (name to value)),
            ),
        )
    }

    private fun addOperator(operator: it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation) {
        val editor = selectedPage()?.let { pageEditors[it.id] }
        if (editor == null) return
        editor.append("\n:- op(${operator.priority}, ${operator.specifier}, ${operator.name}).\n")
    }

    private fun pageTitle(
        page: PageState,
        state: GuiState,
    ): String {
        val dirty =
            (page.content as? PageContent.DocumentReference)
                ?.documentId
                ?.let(state.workspace.documents::get)
                ?.isDirty == true
        val unread =
            page.resolution.hasUnreadChanges ||
                page.console.stdout.hasUnreadChanges ||
                page.console.stderr.hasUnreadChanges ||
                page.console.warnings.hasUnreadChanges ||
                page.diagnostics.hasUnreadChanges
        return buildString {
            append(page.title)
            if (dirty) append("*")
            if (unread) append(" •")
        }
    }

    private fun sourceText(
        page: PageState,
        state: GuiState,
    ): String =
        when (val content = page.content) {
            is PageContent.DocumentReference ->
                state.workspace.documents[content.documentId]
                    ?.text
                    .orEmpty()
            is PageContent.Scratch -> content.text
        }

    private fun statusText(page: PageState): String =
        when (page.resolution.status) {
            ResolutionStatus.IDLE -> "Idle"
            ResolutionStatus.RUNNING -> "Computing ${page.resolution.query.orEmpty()}"
            ResolutionStatus.AWAITING_CONTINUATION -> "Solution available; more may exist"
            ResolutionStatus.COMPLETED -> "Resolution completed"
            ResolutionStatus.FAILED -> "Resolution failed: ${page.resolution.error.orEmpty()}"
            ResolutionStatus.CANCELLED -> "Resolution cancelled"
        }

    private fun clearLowerAreas() {
        solutionsTree.render(emptyList())
        diagnosticsList.render(emptyList())
        operatorsTable.render(emptyList())
        flagsTable.render(emptyList())
        librariesTree.render(emptyList())
        listOf(stdinArea, stdoutArea, stderrArea, warningsArea, staticKbArea, dynamicKbArea).forEach { it.text = "" }
    }

    private fun onEditorTabChanged() {
        if (rendering) return
        val index = editorTabs.selectedIndex
        val pageId =
            renderedState
                ?.workspace
                ?.pages
                ?.getOrNull(index)
                ?.id
        if (pageId == null) return
        dispatch(WorkspaceAction.SelectPage(pageId))
        lowerPanelIds[lowerTabs.selectedIndex]?.let { panel ->
            dispatch(PageAction.MarkPanelRead(pageId, panel))
        }
    }

    private fun onLowerTabChanged() {
        if (rendering) return
        val pageId = renderedState?.workspace?.selectedPageId
        val panel = lowerPanelIds[lowerTabs.selectedIndex]
        if (pageId == null || panel == null) return
        dispatch(PageAction.MarkPanelRead(pageId, panel))
    }

    private fun editorChanged(pageId: PageId) {
        if (rendering) return
        val page = renderedState?.workspace?.page(pageId)
        val text = pageEditors[pageId]?.text
        if (page == null || text == null) return
        when (val content = page.content) {
            is PageContent.DocumentReference -> dispatch(DocumentAction.ChangeText(content.documentId, text))
            is PageContent.Scratch -> dispatch(PageAction.ChangeScratchText(page.id, text))
        }
    }

    private fun queryChanged() {
        if (rendering) return
        if (!browsingQueryHistory) queryHistoryIndex = null
        queryBoundPageId?.let { dispatch(PageAction.ChangeQuery(it, queryField.text)) }
    }

    private fun commitTimeout() {
        if (rendering) return
        val timeout =
            parseDurationInput(timeoutField.text) ?: run {
                showInvalidTimeout()
                return
            }
        queryBoundPageId?.let { dispatch(PageAction.ChangeTimeout(it, timeout)) }
    }

    private fun showInvalidTimeout(): Boolean {
        JOptionPane.showMessageDialog(
            this,
            "Invalid timeout. Expected 0 or none, or a value such as 500ms, 2s 500ms, 1m 30s, 2h, or 1w. " +
                "Units are ms, s, m, h, d, and w; the maximum is one week.",
            "Invalid timeout",
            JOptionPane.ERROR_MESSAGE,
        )
        timeoutField.requestFocusInWindow()
        timeoutField.selectAll()
        return false
    }

    private fun navigateQueryHistory(delta: Int) {
        val history =
            selectedPage()
                ?.history
                ?.resolutions
                ?.map { it.query }
                .orEmpty()
        if (history.isEmpty()) return
        val next =
            when (val index = queryHistoryIndex) {
                null -> if (delta < 0) history.lastIndex else return
                0 -> if (delta > 0) null else 0
                else -> (index + delta).coerceIn(0, history.lastIndex)
            }
        browsingQueryHistory = true
        try {
            if (queryHistoryIndex == null) queryHistoryDraft = queryField.text
            queryHistoryIndex = next
            queryField.text = next?.let(history::get) ?: queryHistoryDraft
        } finally {
            browsingQueryHistory = false
        }
    }

    private fun stdinChanged() {
        if (rendering) return
        stdinBoundPageId?.let { dispatch(PageAction.ChangeStdin(it, stdinArea.text)) }
    }

    private fun caretChanged(event: CaretEvent) {
        val area = event.source as? JTextArea ?: return
        val offset = max(0, event.dot)
        val line = runCatching { area.getLineOfOffset(offset) }.getOrDefault(0)
        val column = runCatching { offset - area.getLineStartOffset(line) }.getOrDefault(0)
        caretLabel.text = "Line $line, column $column"
    }

    private fun selectedPage(): PageState? {
        val state = renderedState ?: return null
        return state.workspace.selectedPageId?.let(state.workspace::page)
    }

    private fun newDocument() = dispatch(WorkspaceAction.NewDocumentPage())

    private fun newScratchPage() = dispatch(WorkspaceAction.NewScratchPage())

    private fun openDocument() = dispatch(WorkspaceAction.RequestOpenDocument)

    private fun requestExit() = dispatch(ApplicationAction.RequestExit)

    private fun closeSelectedPage() {
        selectedPage()?.let { dispatch(WorkspaceAction.RequestClosePage(it.id)) }
    }

    private fun saveSelected(forceSaveAs: Boolean) {
        val documentId = selectedDocumentId() ?: return
        dispatch(DocumentAction.RequestSave(documentId, forceSaveAs))
    }

    private fun reloadSelected() {
        selectedDocumentId()?.let { dispatch(DocumentAction.RequestReload(it)) }
    }

    private fun selectedDocumentId(): DocumentId? =
        (selectedPage()?.content as? PageContent.DocumentReference)?.documentId

    private fun solve(mode: ConsumptionMode) {
        val page = selectedPage() ?: return
        val action =
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) {
                PageAction.Next(page.id, mode)
            } else {
                PageAction.Solve(page.id, mode)
            }
        dispatch(action)
    }

    private fun showAbout() {
        JOptionPane.showMessageDialog(
            this,
            "tuProlog IDE v${Info.VERSION}\nhttps://github.com/tuProlog/2p-kt\nSwing frontend",
            "About tuProlog IDE",
            JOptionPane.INFORMATION_MESSAGE,
        )
    }

    private fun showFileProperties() {
        val page = selectedPage() ?: return
        val documentId = (page.content as? PageContent.DocumentReference)?.documentId
        val document = documentId?.let { renderedState?.workspace?.documents?.get(it) }
        val message =
            buildString {
                append("Name: ").append(document?.displayName ?: page.title)
                append("\nDirty: ").append(document?.isDirty ?: false)
                append("\nRevision: ").append(document?.revision ?: 0)
                document?.origin?.let { append("\nLocation: ").append(it.opaqueReference) }
                append("\nEncoding: UTF-8")
            }
        JOptionPane.showMessageDialog(this, message, "File properties", JOptionPane.INFORMATION_MESSAGE)
    }

    internal fun supportReport(
        thread: Thread = Thread.currentThread(),
        error: Throwable? = null,
    ): String =
        buildString {
            if (error != null) appendLine("Please describe what you were doing before the error.")
            appendLine()
            appendLine("tuProlog IDE v${Info.VERSION}")
            appendLine("Issue tracker: https://github.com/tuProlog/2p-kt/issues")
            appendLine("Thread: ${thread.name}")
            appendLine("Java: ${System.getProperty("java.version")}")
            appendLine("OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")}")
            renderedState?.workspace?.let { workspace ->
                appendLine("Selected page: ${workspace.selectedPageId}")
                workspace.pages.forEach { page ->
                    appendLine(
                        "Page ${page.id}: profile=${page.solverSession.profileId}, " +
                            "solver=${page.solverSession.sessionId}, lifecycle=${page.solverSession.lifecycle}, " +
                            "resolution=${page.resolution.status}",
                    )
                }
            }
            if (error != null) {
                appendLine()
                appendLine("Stack trace:")
                append(SwingIdeUncaughtExceptionHandler.stackTrace(error))
            }
        }

    private fun showReportIssueDialog() {
        val report = supportReport()
        val problemArea = reportIssueProblemArea()
        val reportArea = reportIssueReportArea(report)
        val panel = reportIssuePanel(problemArea, reportArea)
        val copyAndOpen = "Copy report and open GitHub"
        val choice =
            JOptionPane.showOptionDialog(
                this,
                panel,
                "Report an issue",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                arrayOf(copyAndOpen, "Close"),
                copyAndOpen,
            )
        if (choice == 0) copyReportAndOpenIssueTracker(report, problemArea.text)
    }

    private fun reportIssueProblemArea(): JTextArea =
        JTextArea(REPORT_PROBLEM_AREA_ROWS, REPORT_DIALOG_COLUMNS).apply {
            name = "reportIssueProblemArea"
            lineWrap = true
            wrapStyleWord = true
        }

    private fun reportIssueReportArea(report: String): JTextArea =
        JTextArea(report, REPORT_STATUS_AREA_ROWS, REPORT_DIALOG_COLUMNS).apply {
            name = "reportIssueReportArea"
            isEditable = false
            lineWrap = false
            caretPosition = 0
        }

    private fun reportIssuePanel(
        problemArea: JTextArea,
        reportArea: JTextArea,
    ): JComponent =
        JPanel(BorderLayout(0, REPORT_DIALOG_OUTER_GAP)).apply {
            border =
                BorderFactory.createEmptyBorder(
                    REPORT_DIALOG_OUTER_GAP,
                    REPORT_DIALOG_OUTER_GAP,
                    REPORT_DIALOG_OUTER_GAP,
                    REPORT_DIALOG_OUTER_GAP,
                )
            add(
                JPanel(BorderLayout(0, REPORT_DIALOG_INNER_GAP)).apply {
                    add(JLabel("What problem are you noticing? (optional)"), BorderLayout.NORTH)
                    add(JScrollPane(problemArea), BorderLayout.CENTER)
                },
                BorderLayout.NORTH,
            )
            add(
                JPanel(BorderLayout(0, REPORT_DIALOG_INNER_GAP)).apply {
                    add(JLabel("Status report (included automatically):"), BorderLayout.NORTH)
                    add(JScrollPane(reportArea), BorderLayout.CENTER)
                },
                BorderLayout.CENTER,
            )
        }

    private fun copyReportAndOpenIssueTracker(
        report: String,
        problem: String,
    ) {
        val trimmedProblem = problem.trim()
        val fullReport = if (trimmedProblem.isEmpty()) report else "Problem description:\n$trimmedProblem\n\n$report"
        runCatching {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(fullReport), null)
        }
        runCatching {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI("https://github.com/tuProlog/2p-kt/issues/new"))
            }
        }
    }

    private fun selectedEditor(): PrologEditor? = selectedPage()?.id?.let(pageEditors::get)

    private fun dispatch(action: it.unibo.tuprolog.ui.gui.controller.GuiAction) {
        scope.dispatch { controller.dispatch(action) }
    }

    private companion object {
        private const val DEFAULT_FONT_SIZE = 14

        // Metal is the one look-and-feel guaranteed to exist on every JVM, making it the only deterministic
        // "default" to restore to - unlike "System", which varies by OS and may not even be installed here.
        private const val DEFAULT_LOOK_AND_FEEL = "Metal"
        private const val MIN_WINDOW_WIDTH = 900
        private const val MIN_WINDOW_HEIGHT = 650
        private const val PREFERRED_WINDOW_WIDTH = 1200
        private const val PREFERRED_WINDOW_HEIGHT = 820
        private const val WORKSPACE_SPLIT_RESIZE_WEIGHT = 0.58
        private const val WORKSPACE_SPLIT_DIVIDER_LOCATION = 450
        private const val TIMEOUT_FIELD_COLUMNS = 8
        private const val TIMEOUT_FIELD_WIDTH = 100
        private const val QUERY_ROW_HGAP = 8
        private const val QUERY_ROW_BORDER_INSET = 6
        private const val QUERY_BUTTONS_HGAP = 5
        private const val SOLUTIONS_TOOLBAR_HGAP = 4
        private const val STATUS_BAR_VERTICAL_INSET = 3
        private const val STATUS_BAR_HORIZONTAL_INSET = 8
        private const val REPORT_PROBLEM_AREA_ROWS = 6
        private const val REPORT_DIALOG_COLUMNS = 80
        private const val REPORT_STATUS_AREA_ROWS = 12
        private const val REPORT_DIALOG_OUTER_GAP = 8
        private const val REPORT_DIALOG_INNER_GAP = 4

        fun editorArea(): JTextArea = JTextArea().apply { lineWrap = false }

        fun readOnlyArea(): JTextArea = editorArea().apply { isEditable = false }

        fun menuMask(): Int =
            java.awt.Toolkit
                .getDefaultToolkit()
                .menuShortcutKeyMaskEx
    }
}
