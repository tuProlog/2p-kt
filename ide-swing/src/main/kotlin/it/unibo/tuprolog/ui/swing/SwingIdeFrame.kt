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
import kotlinx.coroutines.CoroutineScope
import org.fife.ui.rsyntaxtextarea.ErrorStrip
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.KeyboardFocusManager
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.AbstractAction
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import javax.swing.event.CaretEvent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.DefaultEditorKit
import javax.swing.text.JTextComponent
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

/**
 * Swing view/adapter. It never creates or mutates solver objects: all behaviour goes through [GuiController].
 * The fixed query row is rebound atomically to the selected page and therefore remains semantically page-specific.
 */
class SwingIdeFrame(
    private val controller: GuiController,
    private val scope: CoroutineScope,
    private val featureRenderers: SwingFeatureRendererRegistry = SwingFeatureRendererRegistry(),
) : JFrame() {
    private val editorTabs = JTabbedPane()
    private val lowerTabs = JTabbedPane()
    private val queryField = PrologQueryField()
    private val solveButton = JButton("Solve")
    private val solve10Button = JButton("Solve 10")
    private val solve100Button = JButton("Solve 100")
    private val solveAllButton = JButton("Solve all")
    private val stopButton = JButton("Stop")
    private val resetButton = JButton("Reset")
    private val timeoutField = JTextField("5s", 8)
    private val statusLabel = JLabel("Idle")
    private val caretLabel = JLabel("Line 1, column 1", SwingConstants.RIGHT)

    private val solutionsTree = SolutionTree()
    private val stdinArea = editorArea()
    private val stdoutArea = readOnlyArea()
    private val stderrArea = readOnlyArea()
    private val warningsArea = readOnlyArea()
    private val diagnosticsList = DiagnosticsList()
    private val operatorsTable = OperatorsTable()
    private val flagsTable = FlagsTable()
    private val librariesTree = LibrariesTree()
    private val staticKbArea =
        PrologEditor().apply {
            isEditable = false
            setHighlightCurrentLine(false)
        }
    private val dynamicKbArea =
        PrologEditor().apply {
            isEditable = false
            setHighlightCurrentLine(false)
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
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        minimumSize = Dimension(900, 650)
        preferredSize = Dimension(1200, 820)
        layout = BorderLayout()
        jMenuBar = createMenuBar()

        val vertical =
            JSplitPane(JSplitPane.VERTICAL_SPLIT, editorTabs, createLowerPanel()).apply {
                resizeWeight = 0.58
                dividerLocation = 450
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
        timeoutField.preferredSize = Dimension(100, controlHeight)
        queryField.preferredSize = Dimension(queryField.preferredSize.width, controlHeight)
        listOf(solveButton, solve10Button, solve100Button, solveAllButton, stopButton, resetButton).forEach {
            it.preferredSize = Dimension(88, controlHeight)
        }
        val queryRow =
            JPanel(BorderLayout(8, 0)).apply {
                border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
                add(JLabel("?-"), BorderLayout.WEST)
                add(queryField, BorderLayout.CENTER)
                add(
                    JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0)).apply {
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

        addLowerTab("Solutions", PanelId.SOLUTIONS, solutionsTree)
        addLowerTab("Stdin", PanelId.STDIN, stdinArea)
        addLowerTab("Stdout", PanelId.STDOUT, stdoutArea)
        addLowerTab("Stderr", PanelId.STDERR, stderrArea)
        addLowerTab("Warnings", PanelId.WARNINGS, warningsArea)
        addLowerTab("Diagnostics", PanelId.DIAGNOSTICS, diagnosticsList)
        addLowerTab("Operators", PanelId.OPERATORS, operatorsTable)
        addLowerTab("Flags", PanelId.FLAGS, flagsTable)
        addLowerTab("Libraries", PanelId.LIBRARIES, librariesTree)
        addLowerTab("Static KB", PanelId.STATIC_KB, staticKbArea)
        addLowerTab("Dynamic KB", PanelId.DYNAMIC_KB, dynamicKbArea)

        for (renderer in featureRenderers.all()) {
            val component = renderer.createComponent(featureContext)
            extensionComponents[renderer.featureId] = component
            extensionTabIndices[renderer.featureId] = lowerTabs.tabCount
            lowerTabs.addTab(renderer.displayName, component)
        }

        return JPanel(BorderLayout()).apply {
            add(queryRow, BorderLayout.NORTH)
            add(lowerTabs, BorderLayout.CENTER)
        }
    }

    private fun addLowerTab(
        title: String,
        panelId: PanelId,
        component: JComponent,
    ) {
        val index = lowerTabs.tabCount
        lowerTabs.addTab(
            title,
            if (component is PrologEditor) RTextScrollPane(component, true) else JScrollPane(component),
        )
        lowerPanelIds[index] = panelId
        lowerPanelTitles[index] = title
    }

    private fun createStatusBar(): JComponent =
        JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(3, 8, 3, 8)
            add(statusLabel, BorderLayout.CENTER)
            add(caretLabel, BorderLayout.EAST)
        }

    private fun createMenuBar(): JMenuBar =
        JMenuBar().apply {
            add(
                JMenu("File").apply {
                    mnemonic = KeyEvent.VK_F
                    add(menuItem("New", KeyStroke.getKeyStroke(KeyEvent.VK_N, menuMask())) { newDocument() })
                    add(menuItem("New scratch page", null) { newScratchPage() })
                    add(menuItem("Open…", KeyStroke.getKeyStroke(KeyEvent.VK_O, menuMask())) { openDocument() })
                    addSeparator()
                    add(
                        menuItem(
                            "Close page",
                            KeyStroke.getKeyStroke(KeyEvent.VK_W, menuMask()),
                        ) { closeSelectedPage() },
                    )
                    add(menuItem("Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask())) { saveSelected(false) })
                    add(
                        menuItem(
                            "Save as…",
                            KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask() or InputEvent.SHIFT_DOWN_MASK),
                        ) { saveSelected(true) },
                    )
                    add(menuItem("Reload", null) { reloadSelected() })
                    add(menuItem("File properties", null) { showFileProperties() })
                    addSeparator()
                    add(menuItem("Quit", KeyStroke.getKeyStroke(KeyEvent.VK_Q, menuMask())) { requestExit() })
                },
            )
            add(
                JMenu("Edit").apply {
                    mnemonic = KeyEvent.VK_E
                    add(JMenuItem(DefaultEditorKit.CutAction()).apply { text = "Cut" })
                    add(JMenuItem(DefaultEditorKit.CopyAction()).apply { text = "Copy" })
                    add(JMenuItem(DefaultEditorKit.PasteAction()).apply { text = "Paste" })
                    add(
                        menuItem("Select all", KeyStroke.getKeyStroke(KeyEvent.VK_A, menuMask())) {
                            focusedTextComponent()?.selectAll()
                        },
                    )
                },
            )
            add(
                JMenu("Search").apply {
                    mnemonic = KeyEvent.VK_S
                    add(
                        menuItem(
                            "Find…",
                            KeyStroke.getKeyStroke(KeyEvent.VK_F, menuMask()),
                        ) { searchActions.showFind() },
                    )
                    add(
                        menuItem(
                            "Replace…",
                            KeyStroke.getKeyStroke(KeyEvent.VK_H, menuMask()),
                        ) { searchActions.showReplace() },
                    )
                    add(
                        menuItem(
                            "Go to line…",
                            KeyStroke.getKeyStroke(KeyEvent.VK_G, menuMask()),
                        ) { searchActions.showGoToLine() },
                    )
                },
            )
            add(
                JMenu("Help").apply {
                    add(menuItem("About", null) { showAbout() })
                },
            )
        }

    private fun menuItem(
        label: String,
        accelerator: KeyStroke?,
        action: () -> Unit,
    ): JMenuItem =
        JMenuItem(
            object : AbstractAction(label) {
                override fun actionPerformed(event: java.awt.event.ActionEvent?) = action()
            },
        ).apply { this.accelerator = accelerator }

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
                    parseTimeoutInput(timeoutField.text)?.let { true } ?: showInvalidTimeout()
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
            val component = pageComponents.getOrPut(page.id) { createEditorComponent(page) }
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

    private fun createEditorComponent(page: PageState): JComponent {
        val area =
            PrologEditor().apply {
                document.addDocumentListener(
                    object : DocumentListener {
                        override fun insertUpdate(event: DocumentEvent) = editorChanged(page.id)

                        override fun removeUpdate(event: DocumentEvent) = editorChanged(page.id)

                        override fun changedUpdate(event: DocumentEvent) = editorChanged(page.id)
                    },
                )
                addCaretListener { event -> caretChanged(event) }
            }
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
        val enabled = page != null
        queryField.isEnabled = enabled
        stdinArea.isEnabled = enabled && page.resolution.status != ResolutionStatus.RUNNING

        if (page == null) {
            queryField.text = ""
            clearLowerAreas()
            statusLabel.text = "No page"
            solveButton.isEnabled = false
            solve10Button.isEnabled = false
            solve100Button.isEnabled = false
            solveAllButton.isEnabled = false
            stopButton.isEnabled = false
            resetButton.isEnabled = false
            return
        }

        if (queryField.text != page.query.text) queryField.text = page.query.text
        queryField.highlight(page.solverSession.inspection.operators)
        if (stdinArea.text != page.console.stdin) stdinArea.text = page.console.stdin
        val effective = state.workspace.configuration.resolve(page.configuration)
        val timeoutMs = effective.timeout.inWholeMilliseconds
        if (!timeoutField.isFocusOwner) timeoutField.text = formatTimeoutInput(timeoutMs)

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
        diagnosticsList.render(pageEditors[page.id]?.diagnostics.orEmpty())
        operatorsTable.render(page.solverSession.inspection.operators)
        flagsTable.render(page.solverSession.inspection.flags)
        flagsTable.isEnabled = page.resolution.status != ResolutionStatus.RUNNING
        librariesTree.render(page.solverSession.inspection.libraries)
        staticKbArea.text = page.solverSession.inspection.staticKnowledgeBase
        staticKbArea.highlight(page.solverSession.inspection.operators)
        dynamicKbArea.text = page.solverSession.inspection.dynamicKnowledgeBase
        dynamicKbArea.highlight(page.solverSession.inspection.operators)

        solveButton.text = if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) "Next" else "Solve"
        solve10Button.text =
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) "Next 10" else "Solve 10"
        solve100Button.text =
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) "Next 100" else "Solve 100"
        solveAllButton.text =
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) "All next" else "Solve all"
        solveButton.isEnabled = page.resolution.canSolve || page.resolution.canContinue
        solve10Button.isEnabled = page.resolution.canSolve || page.resolution.canContinue
        solve100Button.isEnabled = page.resolution.canSolve || page.resolution.canContinue
        solveAllButton.isEnabled = page.resolution.canSolve || page.resolution.canContinue
        stopButton.isEnabled = page.resolution.canStop
        resetButton.isEnabled = true
        timeoutField.isEnabled = page.resolution.status != ResolutionStatus.RUNNING
        statusLabel.text = statusText(page)
        updateLowerTabTitles(page)
        acknowledgeVisiblePanel(page)

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
        val panel = lowerPanelIds[lowerTabs.selectedIndex] ?: return
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
        val page = selectedPage() ?: return
        val editor = pageEditors[page.id] ?: return
        val offset =
            diagnostic.range
                ?.start
                ?.offset
                ?.coerceIn(0, editor.document.length) ?: return
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
        val page = selectedPage() ?: return
        val editor = pageEditors[page.id] ?: return
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
                ?.id ?: return
        dispatch(WorkspaceAction.SelectPage(pageId))
        lowerPanelIds[lowerTabs.selectedIndex]?.let { panel ->
            dispatch(PageAction.MarkPanelRead(pageId, panel))
        }
    }

    private fun onLowerTabChanged() {
        if (rendering) return
        val pageId = renderedState?.workspace?.selectedPageId ?: return
        val panel = lowerPanelIds[lowerTabs.selectedIndex] ?: return
        dispatch(PageAction.MarkPanelRead(pageId, panel))
    }

    private fun editorChanged(pageId: PageId) {
        if (rendering) return
        val state = renderedState ?: return
        val page = state.workspace.page(pageId) ?: return
        val text = pageEditors[pageId]?.text ?: return
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
        val milliseconds =
            parseTimeoutInput(timeoutField.text) ?: run {
                showInvalidTimeout()
                return
            }
        queryBoundPageId?.let { dispatch(PageAction.ChangeTimeout(it, milliseconds.milliseconds)) }
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
        caretLabel.text = "Line ${line + 1}, column ${column + 1}"
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
        thread: Thread,
        error: Throwable,
    ): String =
        buildString {
            appendLine("Please describe what you were doing before the error.")
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
            appendLine()
            appendLine("Stack trace:")
            append(SwingIdeUncaughtExceptionHandler.stackTrace(error))
        }

    private fun selectedEditor(): PrologEditor? = selectedPage()?.id?.let(pageEditors::get)

    private fun dispatch(action: it.unibo.tuprolog.ui.gui.controller.GuiAction) {
        scope.dispatch { controller.dispatch(action) }
    }

    private fun focusedTextComponent(): JTextComponent? =
        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner as? JTextComponent

    private companion object {
        fun editorArea(): JTextArea = JTextArea().apply { lineWrap = false }

        fun readOnlyArea(): JTextArea = editorArea().apply { isEditable = false }

        fun menuMask(): Int =
            java.awt.Toolkit
                .getDefaultToolkit()
                .menuShortcutKeyMaskEx
    }
}
