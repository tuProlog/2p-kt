package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.controller.ConsumptionMode
import it.unibo.tuprolog.ui.gui.controller.DocumentAction
import it.unibo.tuprolog.ui.gui.controller.GuiAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.controller.WorkspaceAction
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.PanelId
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.model.resolve
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.FlagPresentation
import it.unibo.tuprolog.ui.gui.presentation.LibraryPresentation
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.formatDurationInput
import it.unibo.tuprolog.ui.gui.presentation.parseDurationInput
import it.unibo.tuprolog.ui.gui.template.TheoryTemplate
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event

/** Builds and refreshes the whole page's DOM from [GuiState]; the sole entry point into the browser document. */
internal class WebIdeView(
    private val controller: GuiController,
    private val scope: CoroutineScope,
    private val templates: List<TheoryTemplate>,
) {
    private val root = document.getElementById("app") as HTMLElement

    private val statusLabel = element("span", "status")
    private val tabBar = element("div", "tab-bar")
    private val editorContainer = element("div", "editor").apply { id = "editor" }
    private val editor = AceEditorView(editorContainer)
    private val queryInput =
        (element("input", null) as HTMLInputElement).apply {
            type = "text"
            id = "query-input"
        }
    private val timeoutInput =
        (element("input", null) as HTMLInputElement).apply {
            type = "text"
            id = "timeout-input"
        }
    private val stdinArea = element("textarea", null) as HTMLTextAreaElement
    private val solveButton = element("button", null) as HTMLButtonElement
    private val solve10Button = (element("button", null) as HTMLButtonElement).apply { textContent = "Solve 10" }
    private val solveAllButton = (element("button", null) as HTMLButtonElement).apply { textContent = "Solve all" }
    private val stopButton = (element("button", null) as HTMLButtonElement).apply { textContent = "Stop" }
    private val resetButton = (element("button", null) as HTMLButtonElement).apply { textContent = "Reset" }
    private val clearSolutionsButton =
        (element("button", null) as HTMLButtonElement).apply {
            textContent =
                "Clear solutions"
        }
    private val sideTabBar = element("div", "side-tab-bar")
    private val panelContents = PanelId.entries.associateWith { element("div", "side-content") }
    private val panelTabs = mutableMapOf<PanelId, HTMLElement>()

    private var renderedState: GuiState? = null
    private var selectedSidePanel: PanelId = PanelId.SOLUTIONS
    private var rendering = false

    init {
        root.appendChild(menuBar())
        root.appendChild(tabBar)
        root.appendChild(mainArea())
        installListeners()
        selectSidePanel(PanelId.SOLUTIONS)
        editor.resize()
        kotlinx.browser.window.addEventListener("resize", { _: Event -> editor.resize() })
    }

    fun render(state: GuiState) {
        rendering = true
        try {
            renderedState = state
            renderTabs(state)
            renderSelectedPage(state)
        } finally {
            rendering = false
        }
    }

    // region shell

    private fun menuBar(): HTMLElement =
        element("div", "menu-bar").apply {
            appendChild(button("New") { dispatch(WorkspaceAction.NewDocumentPage()) })
            appendChild(button("New scratch") { dispatch(WorkspaceAction.NewScratchPage()) })
            if (templates.isNotEmpty()) appendChild(templatesMenu())
            appendChild(button("Open…") { pickOpen() })
            appendChild(button("Save") { selectedPage()?.let { save(it, false) } })
            appendChild(button("Save as…") { selectedPage()?.let { save(it, true) } })
            appendChild(
                button("Close page") { selectedPage()?.let { dispatch(WorkspaceAction.RequestClosePage(it.id)) } },
            )
            appendChild(statusLabel)
        }

    private fun templatesMenu(): HTMLElement {
        val select = element("select", null) as HTMLSelectElement
        select.appendChild(
            (document.createElement("option") as HTMLElement).apply { textContent = "New from template…" },
        )
        templates.forEach { template ->
            val option = document.createElement("option") as HTMLElement
            option.textContent = template.displayName
            option.setAttribute("value", template.id)
            select.appendChild(option)
        }
        select.addEventListener(
            "change",
            { _: Event ->
                val template = templates.firstOrNull { it.id == select.value }
                if (template != null) dispatch(WorkspaceAction.NewDocumentPage("${template.id}.pl", template.source))
                select.selectedIndex = 0
            },
        )
        return select
    }

    private fun mainArea(): HTMLElement =
        element("div", "main").apply {
            appendChild(queryBar())
            appendChild(
                element("div", "split").apply {
                    appendChild(editorContainer)
                    appendChild(sidePanel())
                },
            )
        }

    private fun queryBar(): HTMLElement =
        element("div", "query-bar").apply {
            appendChild(queryInput)
            appendChild(solveButton)
            appendChild(solve10Button)
            appendChild(solveAllButton)
            appendChild(stopButton)
            appendChild(resetButton)
            appendChild(element("span", null).apply { textContent = "Timeout" })
            appendChild(timeoutInput)
        }

    private fun sidePanel(): HTMLElement =
        element("div", "side").apply {
            appendChild(sideTabBar)
            PanelId.entries.forEach { panel ->
                val tab = element("div", "side-tab").apply { textContent = panelTitle(panel) }
                tab.addEventListener("click", { _: Event -> selectSidePanel(panel) })
                panelTabs[panel] = tab
                sideTabBar.appendChild(tab)
            }
            PanelId.entries.forEach { panel ->
                val content = panelContents.getValue(panel)
                if (panel == PanelId.SOLUTIONS) content.appendChild(clearSolutionsButton)
                if (panel == PanelId.STDIN) {
                    content.textContent = ""
                    content.appendChild(stdinArea)
                }
                appendChild(content)
            }
        }

    private fun panelTitle(panel: PanelId): String =
        when (panel) {
            PanelId.SOLUTIONS -> "Solutions"
            PanelId.STDIN -> "Stdin"
            PanelId.STDOUT -> "Stdout"
            PanelId.STDERR -> "Stderr"
            PanelId.WARNINGS -> "Warnings"
            PanelId.DIAGNOSTICS -> "Diagnostics"
            PanelId.OPERATORS -> "Operators"
            PanelId.FLAGS -> "Flags"
            PanelId.LIBRARIES -> "Libraries"
            PanelId.STATIC_KB -> "Static KB"
            PanelId.DYNAMIC_KB -> "Dynamic KB"
        }

    private fun selectSidePanel(panel: PanelId) {
        selectedSidePanel = panel
        panelTabs.forEach { (id, tab) -> tab.className = if (id == panel) "side-tab selected" else "side-tab" }
        panelContents.forEach { (id, content) -> content.style.display = if (id == panel) "block" else "none" }
        if (!rendering) selectedPage()?.let { dispatch(PageAction.MarkPanelRead(it.id, panel)) }
    }

    // endregion

    // region listeners

    private fun installListeners() {
        editor.onChange {
            if (rendering) return@onChange
            val page = selectedPage() ?: return@onChange
            when (page.content) {
                is PageContent.DocumentReference ->
                    dispatch(
                        DocumentAction.ChangeText(
                            (page.content as PageContent.DocumentReference).documentId,
                            editor.value,
                        ),
                    )
                is PageContent.Scratch -> dispatch(PageAction.ChangeScratchText(page.id, editor.value))
            }
        }
        queryInput.addEventListener(
            "input",
            { _: Event ->
                if (rendering) return@addEventListener
                selectedPage()?.let { dispatch(PageAction.ChangeQuery(it.id, queryInput.value)) }
            },
        )
        stdinArea.addEventListener(
            "input",
            { _: Event ->
                if (rendering) return@addEventListener
                selectedPage()?.let { dispatch(PageAction.ChangeStdin(it.id, stdinArea.value)) }
            },
        )
        timeoutInput.addEventListener(
            "change",
            { _: Event ->
                val page = selectedPage() ?: return@addEventListener
                val timeout = parseDurationInput(timeoutInput.value)
                if (timeout == null) {
                    kotlinx.browser.window.alert(
                        "Invalid timeout. Try values such as 500ms, 2s 500ms, 1m 30s, 2h, or 1w.",
                    )
                } else {
                    dispatch(PageAction.ChangeTimeout(page.id, timeout))
                }
            },
        )
        solveButton.addEventListener("click", { _: Event -> solveOrNext(ConsumptionMode.ONE) })
        solve10Button.addEventListener("click", { _: Event -> solveOrNext(ConsumptionMode.TEN) })
        solveAllButton.addEventListener("click", { _: Event -> solveOrNext(ConsumptionMode.ALL) })
        stopButton.addEventListener("click", { _: Event -> selectedPage()?.let { dispatch(PageAction.Stop(it.id)) } })
        resetButton.addEventListener("click", { _: Event -> selectedPage()?.let { dispatch(PageAction.Reset(it.id)) } })
        clearSolutionsButton.addEventListener(
            "click",
            { _: Event -> selectedPage()?.let { dispatch(PageAction.ClearHistory(it.id)) } },
        )
    }

    private fun solveOrNext(mode: ConsumptionMode) {
        val page = selectedPage() ?: return
        val action =
            if (page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION) {
                PageAction.Next(page.id, mode)
            } else {
                PageAction.Solve(page.id, mode)
            }
        dispatch(action)
    }

    private fun pickOpen() {
        dispatch(WorkspaceAction.RequestOpenDocument)
    }

    private fun save(
        page: PageState,
        forceSaveAs: Boolean,
    ) {
        val documentId = (page.content as? PageContent.DocumentReference)?.documentId ?: return
        dispatch(DocumentAction.RequestSave(documentId, forceSaveAs))
    }

    // endregion

    // region rendering

    private fun renderTabs(state: GuiState) {
        tabBar.innerHTML = ""
        state.workspace.pages.forEach { page ->
            val doc = (page.content as? PageContent.DocumentReference)?.let { state.workspace.document(it.documentId) }
            val tab = element("div", if (page.id == state.workspace.selectedPageId) "tab selected" else "tab")
            val label = element("span", if (doc?.isDirty == true) "dirty" else null)
            label.textContent = page.title
            tab.appendChild(label)
            tab.addEventListener("click", { _: Event -> dispatch(WorkspaceAction.SelectPage(page.id)) })
            tabBar.appendChild(tab)
        }
    }

    private fun renderSelectedPage(state: GuiState) {
        val page = selectedPage()
        if (page == null) {
            editor.value = ""
            editor.readOnly = true
            statusLabel.textContent = "No page"
            return
        }
        editor.readOnly = false
        val text =
            when (val content = page.content) {
                is PageContent.DocumentReference ->
                    state.workspace
                        .document(content.documentId)
                        ?.text
                        .orEmpty()
                is PageContent.Scratch -> content.text
            }
        if (!editor.isFocused) editor.value = text
        editor.setDiagnostics(page.diagnostics.values)
        editor.setSemanticTokens(page.semanticTokens)
        if (document.activeElement != queryInput) queryInput.value = page.query.text
        if (document.activeElement != stdinArea) stdinArea.value = page.console.stdin
        val effectiveTimeout =
            state.workspace.configuration
                .resolve(page.configuration)
                .timeout
        if (document.activeElement != timeoutInput) timeoutInput.value = formatDurationInput(effectiveTimeout)

        val awaiting = page.resolution.status == ResolutionStatus.AWAITING_CONTINUATION
        solveButton.textContent = if (awaiting) "Next" else "Solve"
        solve10Button.textContent = if (awaiting) "Next 10" else "Solve 10"
        solveAllButton.textContent = if (awaiting) "All next" else "Solve all"
        val canSolve = page.resolution.canSolve || page.resolution.canContinue
        solveButton.disabled = !canSolve
        solve10Button.disabled = !canSolve
        solveAllButton.disabled = !canSolve
        stopButton.disabled = !page.resolution.canStop
        statusLabel.textContent = "Resolution: ${page.resolution.status}"

        renderPanelTabBadges(page)
        renderSolutions(page)
        renderConsole(page)
        renderDiagnostics(page)
        renderInspection(page)
    }

    private fun renderPanelTabBadges(page: PageState) {
        fun badge(
            panel: PanelId,
            unread: Boolean,
        ) {
            val tab = panelTabs.getValue(panel)
            tab.className =
                buildString {
                    append(if (panel == selectedSidePanel) "side-tab selected" else "side-tab")
                    if (unread) append(" unread")
                }
        }
        badge(PanelId.SOLUTIONS, page.resolution.hasUnreadChanges)
        badge(PanelId.STDOUT, page.console.stdout.hasUnreadChanges)
        badge(PanelId.STDERR, page.console.stderr.hasUnreadChanges)
        badge(PanelId.WARNINGS, page.console.warnings.hasUnreadChanges)
        badge(PanelId.DIAGNOSTICS, page.diagnostics.hasUnreadChanges)
        badge(PanelId.STDIN, false)
        badge(PanelId.OPERATORS, false)
        badge(PanelId.FLAGS, false)
        badge(PanelId.LIBRARIES, false)
        badge(PanelId.STATIC_KB, false)
        badge(PanelId.DYNAMIC_KB, false)
    }

    private fun renderSolutions(page: PageState) {
        val container = panelContents.getValue(PanelId.SOLUTIONS)
        container.innerHTML = ""
        container.appendChild(clearSolutionsButton)
        val list = element("ul", "solutions")
        val entries = solutionEntries(page)
        entries.forEach { entry ->
            val item = element("li", null)
            val header = element("div", "query")
            header.textContent = "?- ${entry.first}"
            header.addEventListener("click", { _: Event -> dispatch(PageAction.ChangeQuery(page.id, entry.first)) })
            item.appendChild(header)
            entry.second.forEach { solution -> item.appendChild(solutionLine(solution)) }
            list.appendChild(item)
        }
        container.appendChild(list)
    }

    private fun solutionEntries(page: PageState): List<Pair<String, List<SolutionPresentation>>> {
        val history = page.history.resolutions.map { it.query to it.solutions }
        val current = page.resolution
        val live =
            if (current.query != null &&
                current.status in setOf(ResolutionStatus.RUNNING, ResolutionStatus.AWAITING_CONTINUATION)
            ) {
                listOf(current.query!! to current.solutions)
            } else {
                emptyList()
            }
        return history + live
    }

    private fun solutionLine(solution: SolutionPresentation): HTMLElement {
        val line = element("div", null)
        line.textContent =
            when (solution) {
                is SolutionPresentation.Yes ->
                    "yes" +
                        (solution.solvedQuery?.let { ": $it" } ?: "") +
                        solution.bindings.joinToString("") { "\n  ${it.variable} = ${it.value}" }
                is SolutionPresentation.No -> "no"
                is SolutionPresentation.Halt -> "error: ${solution.message}"
            }
        return line
    }

    private fun renderConsole(page: PageState) {
        panelContents.getValue(PanelId.STDOUT).textContent = page.console.stdout.text
        panelContents.getValue(PanelId.STDERR).textContent = page.console.stderr.text
        panelContents.getValue(PanelId.WARNINGS).textContent =
            page.console.warnings.values.joinToString("\n\n") { warning ->
                buildString {
                    append(warning.message)
                    if (warning.logicStackTrace.isNotEmpty()) {
                        append("\n").append(warning.logicStackTrace.joinToString("\n"))
                    }
                }
            }
    }

    private fun renderDiagnostics(page: PageState) {
        val container = panelContents.getValue(PanelId.DIAGNOSTICS)
        container.innerHTML = ""
        val list = element("ul", null)
        page.diagnostics.values.forEach { diagnostic -> list.appendChild(diagnosticLine(diagnostic)) }
        container.appendChild(list)
    }

    private fun diagnosticLine(diagnostic: Diagnostic): HTMLElement {
        val item = element("li", "diagnostic ${diagnostic.severity}")
        val location =
            diagnostic.range
                ?.start
                ?.let { " (line ${it.line + 1}, column ${it.column + 1})" }
                .orEmpty()
        item.textContent = "${diagnostic.severity}: ${diagnostic.message}$location"
        return item
    }

    private fun renderInspection(page: PageState) {
        val inspection = page.solverSession.inspection
        panelContents.getValue(PanelId.OPERATORS).replaceWith(operatorsTable(inspection.operators))
        panelContents.getValue(PanelId.FLAGS).replaceWith(flagsTable(inspection.flags))
        panelContents.getValue(PanelId.LIBRARIES).replaceWith(librariesList(inspection.libraries))
        panelContents.getValue(PanelId.STATIC_KB).textContent = inspection.staticKnowledgeBase
        panelContents.getValue(PanelId.DYNAMIC_KB).textContent = inspection.dynamicKnowledgeBase
    }

    private fun HTMLElement.replaceWith(content: HTMLElement) {
        innerHTML = ""
        appendChild(content)
    }

    private fun operatorsTable(operators: List<OperatorPresentation>): HTMLElement {
        val table = element("table", null)
        table.appendChild(tableRow(listOf("Name", "Priority", "Specifier"), header = true))
        operators.forEach { table.appendChild(tableRow(listOf(it.name, it.priority.toString(), it.specifier))) }
        return table
    }

    private fun flagsTable(flags: List<FlagPresentation>): HTMLElement {
        val table = element("table", null)
        table.appendChild(tableRow(listOf("Name", "Value"), header = true))
        flags.forEach { table.appendChild(tableRow(listOf(it.name, it.value))) }
        return table
    }

    private fun librariesList(libraries: List<LibraryPresentation>): HTMLElement {
        val list = element("div", null)
        libraries.forEach { library ->
            val entry = element("div", null)
            entry.innerHTML =
                "<strong>${library.alias}</strong><br/>" +
                "Predicates: ${library.predicates.joinToString(", ")}<br/>" +
                "Functions: ${library.functions.joinToString(", ")}"
            list.appendChild(entry)
        }
        return list
    }

    private fun tableRow(
        cells: List<String>,
        header: Boolean = false,
    ): HTMLElement {
        val row = element("tr", null)
        cells.forEach { cell ->
            val tag = if (header) "th" else "td"
            row.appendChild((document.createElement(tag) as HTMLElement).apply { textContent = cell })
        }
        return row
    }

    // endregion

    private fun selectedPage(): PageState? {
        val state = renderedState ?: return null
        return state.workspace.pages.firstOrNull { it.id == state.workspace.selectedPageId }
    }

    private fun dispatch(action: GuiAction) {
        scope.launch { controller.dispatch(action) }
    }

    private fun element(
        tag: String,
        className: String?,
    ): HTMLElement =
        (document.createElement(tag) as HTMLElement).apply {
            if (className != null) this.className = className
        }

    private fun button(
        label: String,
        onClick: () -> Unit,
    ): HTMLElement =
        (document.createElement("button") as HTMLButtonElement).apply {
            textContent = label
            addEventListener("click", { _: Event -> onClick() })
        }
}
