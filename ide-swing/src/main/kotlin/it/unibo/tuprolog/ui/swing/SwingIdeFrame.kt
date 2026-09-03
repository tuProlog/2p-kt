package it.unibo.tuprolog.ui.swing

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
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlinx.coroutines.CoroutineScope
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.KeyboardFocusManager
import java.awt.event.InputEvent
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
import javax.swing.JSpinner
import javax.swing.JSplitPane
import javax.swing.JTabbedPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.SpinnerNumberModel
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
    private val queryField = JTextField()
    private val solveButton = JButton("Solve")
    private val solveAllButton = JButton("Solve all")
    private val stopButton = JButton("Stop")
    private val resetButton = JButton("Reset")
    private val timeoutSpinner = JSpinner(SpinnerNumberModel(5000L, 1L, Long.MAX_VALUE, 250L))
    private val statusLabel = JLabel("Idle")
    private val caretLabel = JLabel("Line 1, column 1", SwingConstants.RIGHT)

    private val solutionsArea = readOnlyArea()
    private val stdinArea = editorArea()
    private val stdoutArea = readOnlyArea()
    private val stderrArea = readOnlyArea()
    private val warningsArea = readOnlyArea()
    private val diagnosticsArea = readOnlyArea()
    private val operatorsArea = readOnlyArea()
    private val flagsArea = readOnlyArea()
    private val librariesArea = readOnlyArea()
    private val staticKbArea = readOnlyArea()
    private val dynamicKbArea = readOnlyArea()

    private val pageEditors = linkedMapOf<PageId, JTextArea>()
    private val pageComponents = linkedMapOf<PageId, JComponent>()
    private val lowerPanelIds = mutableMapOf<Int, PanelId>()
    private val lowerPanelTitles = mutableMapOf<Int, String>()
    private val extensionComponents = mutableMapOf<it.unibo.tuprolog.ui.gui.identity.FeatureId, JComponent>()
    private val extensionTabIndices = mutableMapOf<it.unibo.tuprolog.ui.gui.identity.FeatureId, Int>()
    private val featureContext = SwingFeatureContext(controller, scope)

    private var renderedState: GuiState? = null
    private var queryBoundPageId: PageId? = null
    private var stdinBoundPageId: PageId? = null
    private var rendering = false

    init {
        title = "2P-Kt IDE — Swing"
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
            title = "${state.application.metadata.productName} ${state.application.metadata.version} — Swing"
        } finally {
            rendering = false
        }
    }

    private fun createLowerPanel(): JComponent {
        val queryRow =
            JPanel(BorderLayout(8, 0)).apply {
                border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
                add(JLabel("?-"), BorderLayout.WEST)
                add(queryField, BorderLayout.CENTER)
                add(
                    JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0)).apply {
                        add(solveButton)
                        add(solveAllButton)
                        add(stopButton)
                        add(resetButton)
                        add(JLabel("Timeout ms"))
                        add(timeoutSpinner)
                    },
                    BorderLayout.EAST,
                )
            }

        addLowerTab("Solutions", PanelId.SOLUTIONS, solutionsArea)
        addLowerTab("Stdin", PanelId.STDIN, stdinArea)
        addLowerTab("Stdout", PanelId.STDOUT, stdoutArea)
        addLowerTab("Stderr", PanelId.STDERR, stderrArea)
        addLowerTab("Warnings", PanelId.WARNINGS, warningsArea)
        addLowerTab("Diagnostics", PanelId.DIAGNOSTICS, diagnosticsArea)
        addLowerTab("Operators", PanelId.OPERATORS, operatorsArea)
        addLowerTab("Flags", PanelId.FLAGS, flagsArea)
        addLowerTab("Libraries", PanelId.LIBRARIES, librariesArea)
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
        area: JTextArea,
    ) {
        val index = lowerTabs.tabCount
        lowerTabs.addTab(title, JScrollPane(area))
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
        queryField.addActionListener { solve(ConsumptionMode.ONE) }
        solveButton.addActionListener {
            val page = selectedPage() ?: return@addActionListener
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) {
                dispatch(PageAction.Next(page.id, ConsumptionMode.ONE))
            } else {
                dispatch(PageAction.Solve(page.id, ConsumptionMode.ONE))
            }
        }
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
        timeoutSpinner.addChangeListener {
            if (!rendering) {
                val pageId = queryBoundPageId ?: return@addChangeListener
                val milliseconds = (timeoutSpinner.value as Number).toLong().coerceAtLeast(1)
                dispatch(PageAction.ChangeTimeout(pageId, milliseconds.milliseconds))
            }
        }
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
            if (component != null) editorTabs.remove(component)
        }

        pages.forEachIndexed { index, page ->
            val component = pageComponents.getOrPut(page.id) { createEditorComponent(page) }
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
        }

        val selectedIndex = pages.indexOfFirst { it.id == state.workspace.selectedPageId }
        if (selectedIndex >= 0 && editorTabs.selectedIndex != selectedIndex) {
            editorTabs.selectedIndex = selectedIndex
        }
    }

    private fun createEditorComponent(page: PageState): JComponent {
        val area =
            editorArea().apply {
                font = Font(Font.MONOSPACED, Font.PLAIN, 14)
                tabSize = 4
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
        return JScrollPane(area)
    }

    private fun renderSelectedPage(state: GuiState) {
        val page = state.workspace.selectedPageId?.let(state.workspace::page)
        queryBoundPageId = page?.id
        stdinBoundPageId = page?.id
        val enabled = page != null
        queryField.isEnabled = enabled
        stdinArea.isEnabled = enabled && page?.resolution?.status != ResolutionStatus.RUNNING

        if (page == null) {
            queryField.text = ""
            clearLowerAreas()
            statusLabel.text = "No page"
            solveButton.isEnabled = false
            solveAllButton.isEnabled = false
            stopButton.isEnabled = false
            resetButton.isEnabled = false
            return
        }

        if (queryField.text != page.query.text) queryField.text = page.query.text
        if (stdinArea.text != page.console.stdin) stdinArea.text = page.console.stdin
        val effective = state.workspace.configuration.resolve(page.configuration)
        val timeoutMs = effective.timeout.inWholeMilliseconds.coerceAtLeast(1)
        if ((timeoutSpinner.value as Number).toLong() != timeoutMs) timeoutSpinner.value = timeoutMs

        solutionsArea.text = formatSolutions(page)
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
        diagnosticsArea.text =
            page.diagnostics.values.joinToString("\n") { diagnostic ->
                "${diagnostic.severity}: ${diagnostic.message}"
            }
        operatorsArea.text =
            page.solverSession.inspection.operators
                .joinToString("\n") { "${it.priority} ${it.specifier} ${it.name}" }
        flagsArea.text =
            page.solverSession.inspection.flags
                .joinToString("\n") { "${it.name} = ${it.value}" }
        librariesArea.text =
            page.solverSession.inspection.libraries.joinToString("\n\n") { library ->
                buildString {
                    append(library.alias)
                    if (library.predicates.isNotEmpty()) {
                        append(
                            "\n  predicates: ",
                        ).append(library.predicates.joinToString())
                    }
                    if (library.functions.isNotEmpty()) {
                        append(
                            "\n  functions: ",
                        ).append(library.functions.joinToString())
                    }
                    if (library.operators.isNotEmpty()) {
                        append(
                            "\n  operators: ",
                        ).append(library.operators.joinToString { it.name })
                    }
                }
            }
        staticKbArea.text = page.solverSession.inspection.staticKnowledgeBase
        dynamicKbArea.text = page.solverSession.inspection.dynamicKnowledgeBase

        solveButton.text = if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) "Next" else "Solve"
        solveAllButton.text =
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) "All next" else "Solve all"
        solveButton.isEnabled = page.resolution.canSolve || page.resolution.canContinue
        solveAllButton.isEnabled = page.resolution.canSolve || page.resolution.canContinue
        stopButton.isEnabled = page.resolution.canStop
        resetButton.isEnabled = true
        timeoutSpinner.isEnabled = page.resolution.status != ResolutionStatus.RUNNING
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
                    else -> false
                }
            val title = if (unread) "$baseTitle*" else baseTitle
            if (lowerTabs.getTitleAt(index) != title) lowerTabs.setTitleAt(index, title)
        }
    }

    private fun acknowledgeVisiblePanel(page: PageState) {
        val panel = lowerPanelIds[lowerTabs.selectedIndex] ?: return
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

    private fun formatSolutions(page: PageState): String =
        page.resolution.solutions.joinToString("\n\n") { solution ->
            when (solution) {
                is SolutionPresentation.Yes ->
                    if (solution.bindings.isEmpty()) {
                        "yes."
                    } else {
                        solution.bindings.joinToString(",\n") { "${it.variable} = ${it.value}" } + "."
                    }
                is SolutionPresentation.No -> "no."
                is SolutionPresentation.Halt ->
                    buildString {
                        append(if (solution.isTimeout) "timeout" else "halt")
                        append(": ").append(solution.message)
                        if (solution.logicStackTrace.isNotEmpty()) {
                            append("\n").append(solution.logicStackTrace.joinToString("\n"))
                        }
                    }
            }
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
        listOf(
            solutionsArea,
            stdinArea,
            stdoutArea,
            stderrArea,
            warningsArea,
            diagnosticsArea,
            operatorsArea,
            flagsArea,
            librariesArea,
            staticKbArea,
            dynamicKbArea,
        ).forEach { it.text = "" }
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
        queryBoundPageId?.let { dispatch(PageAction.ChangeQuery(it, queryField.text)) }
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
        val metadata = renderedState?.application?.metadata ?: return
        JOptionPane.showMessageDialog(
            this,
            "${metadata.productName} ${metadata.version}\n${metadata.homepage}\nSwing frontend",
            "About ${metadata.productName}",
            JOptionPane.INFORMATION_MESSAGE,
        )
    }

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
