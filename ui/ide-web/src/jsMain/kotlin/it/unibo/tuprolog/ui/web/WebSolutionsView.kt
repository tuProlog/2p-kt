package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.controller.GuiAction
import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.ResolutionHistoryEntry
import it.unibo.tuprolog.ui.gui.model.ResolutionStatus
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import it.unibo.tuprolog.ui.gui.presentation.detailLines
import it.unibo.tuprolog.ui.gui.presentation.keyword
import it.unibo.tuprolog.ui.gui.presentation.logicText
import it.unibo.tuprolog.ui.gui.presentation.queryLabel
import it.unibo.tuprolog.ui.gui.presentation.resultLabel
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

/** Collapsible solution history with the same copy, query and delete actions as Swing. */
internal class WebSolutionsView(
    private val container: HTMLElement,
    private val dispatch: (GuiAction) -> Unit,
    private val reportError: (String) -> Unit,
) {
    private val expanded = mutableMapOf<String, Boolean>()
    private val renderedKeys = mutableSetOf<String>()
    private var menu: HTMLElement? = null
    private var menuOwner: HTMLElement? = null

    init {
        document.addEventListener("click", { _: Event -> closeMenu() })
        document.addEventListener("keydown", { event: Event ->
            if ((event as KeyboardEvent).key == "Escape") closeMenu(restoreFocus = true)
        })
    }

    fun render(page: PageState) {
        closeMenu()
        renderedKeys.clear()
        container.textContent = ""
        container.classList.add("solutions-panel")
        val list = element("ul", "solutions")
        page.history.resolutions.forEachIndexed { index, entry ->
            list.appendChild(queryNode(page, entry, "$index", historical = true))
        }
        val current = page.resolution
        if (current.query != null &&
            current.status in setOf(ResolutionStatus.RUNNING, ResolutionStatus.AWAITING_CONTINUATION)
        ) {
            val entry =
                ResolutionHistoryEntry(
                    current.query!!,
                    current.solutions,
                    current.status,
                    submittedAt = current.submittedAt,
                )
            list.appendChild(queryNode(page, entry, "live", historical = false))
        }
        expanded.keys.retainAll(renderedKeys)
        val clear = element("button", "clear-solutions") as HTMLButtonElement
        clear.id = "clear-solutions-button"
        clear.textContent = "Clear solutions"
        clear.title = "Remove concluded queries; a query still running is kept"
        clear.addEventListener("click", { _: Event -> dispatch(PageAction.ClearHistory(page.id)) })
        container.appendChild(
            element("div", "solution-layout").apply {
                appendChild(list)
                appendChild(clear)
            },
        )
    }

    private fun queryNode(
        page: PageState,
        entry: ResolutionHistoryEntry,
        index: String,
        historical: Boolean,
    ): HTMLElement {
        val key = "${page.id}/${entry.submittedAt}/${entry.query}/$index"
        val children = entry.solutions.mapIndexed { i, solution -> resultNode(solution, i + 1, "$key/$i") }
        val label = queryLabel(entry.query, entry.submittedAt)
        val fullText = label + children.joinToString("") { "\n" + it.text.prependIndent("  ") }
        return row(label, entry.query, "?-", "query", key, children, fullText, page, entry, historical).element
    }

    private fun resultNode(
        solution: SolutionPresentation,
        number: Int,
        key: String,
    ): Row {
        val children =
            solution.detailLines.mapIndexed { index, text ->
                row(text, text, null, "detail", "$key/$index", emptyList(), text)
            }
        val label = solution.resultLabel(number)
        val text = label + children.joinToString("") { "\n" + it.text.prependIndent("  ") }
        return row(label, solution.logicText, solution.keyword, "result", key, children, text)
    }

    private data class Row(
        val element: HTMLElement,
        val text: String,
    )

    @Suppress("LongParameterList")
    private fun row(
        label: String,
        logic: String,
        keyword: String?,
        kind: String,
        key: String,
        children: List<Row>,
        fullText: String,
        page: PageState? = null,
        entry: ResolutionHistoryEntry? = null,
        historical: Boolean = false,
    ): Row {
        val item = element("li", null)
        val branch = if (children.isNotEmpty()) element("details", null) else null
        val line = element(if (branch == null) "div" else "summary", "solution-row $kind")
        line.tabIndex = 0
        if (keyword != null) {
            val icon = element("img", "tab-icon")
            val iconName =
                if (keyword ==
                    "?-"
                ) {
                    "query"
                } else if (keyword == "timeout") {
                    "halt-solution"
                } else {
                    "$keyword-solution"
                }
            icon.setAttribute("src", "icons/$iconName.png")
            icon.setAttribute("alt", "")
            line.appendChild(icon)
        }
        val offset = keyword?.let(label::indexOf) ?: -1
        if (offset >= 0) {
            line.appendChild(document.createTextNode(label.take(offset)))
            line.appendChild(element("strong", "outcome").apply { textContent = keyword })
            line.appendChild(document.createTextNode(label.drop(offset + keyword.orEmpty().length)))
        } else {
            line.textContent = label
        }
        if (branch != null) {
            renderedKeys += key
            branch.asDynamic().open = expanded[key] ?: true
            branch.addEventListener("toggle", { _: Event ->
                if (container.contains(branch)) {
                    expanded[key] =
                        branch.asDynamic().open as Boolean
                }
            })
            branch.appendChild(line)
            branch.appendChild(element("ul", null).apply { children.forEach { appendChild(it.element) } })
            item.appendChild(branch)
        } else {
            item.appendChild(line)
        }

        installMenu(line, branch, key, fullText, logic, page, entry, historical)
        return Row(item, fullText)
    }

    @Suppress("LongParameterList")
    private fun installMenu(
        line: HTMLElement,
        branch: HTMLElement?,
        key: String,
        fullText: String,
        logic: String,
        page: PageState?,
        entry: ResolutionHistoryEntry?,
        historical: Boolean,
    ) {
        fun openMenu(
            x: Int,
            y: Int,
        ) {
            showMenu(line, x, y) { popup ->
                fun action(
                    label: String,
                    id: String,
                    enabled: Boolean = true,
                    handler: () -> Unit,
                ) {
                    val button = element("button", null) as HTMLButtonElement
                    button.textContent = label
                    button.setAttribute("role", "menuitem")
                    button.setAttribute("data-action", id)
                    button.disabled = !enabled
                    button.addEventListener("click", { event: Event ->
                        event.stopPropagation()
                        closeMenu(restoreFocus = true)
                        handler()
                    })
                    popup.appendChild(button)
                }
                action("Copy full text", "copy-full") { copyText(fullText) }
                action("Copy logic text", "copy-logic") { copyText(logic) }
                if (page != null && entry != null) {
                    action("Delete", "delete", historical) { dispatch(PageAction.DeleteResolution(page.id, entry)) }
                    action("Solve this", "solve-this") {
                        val input = document.getElementById("query-input") as HTMLInputElement
                        input.value = entry.query
                        dispatch(PageAction.ChangeQuery(page.id, entry.query))
                        input.focus()
                    }
                }
                if (branch != null) {
                    val isOpen = branch.asDynamic().open as Boolean
                    action(if (isOpen) "Collapse" else "Expand", "toggle") {
                        expanded[key] = !isOpen
                        branch.asDynamic().open = !isOpen
                    }
                }
            }
        }
        line.addEventListener("contextmenu", { event: Event ->
            event as MouseEvent
            event.preventDefault()
            event.stopPropagation()
            openMenu(event.clientX, event.clientY)
        })
        line.addEventListener("keydown", { event: Event ->
            event as KeyboardEvent
            if (event.key == "ContextMenu" || (event.shiftKey && event.key == "F10")) {
                event.preventDefault()
                val rect = line.getBoundingClientRect()
                openMenu(rect.left.toInt(), rect.bottom.toInt())
            }
        })
    }

    private fun showMenu(
        owner: HTMLElement,
        x: Int,
        y: Int,
        build: (HTMLElement) -> Unit,
    ) {
        closeMenu()
        menuOwner = owner
        val popup = element("div", "solution-menu")
        popup.setAttribute("role", "menu")
        popup.setAttribute("aria-label", "Solution actions")
        build(popup)
        document.body!!.appendChild(popup)
        popup.style.left = minOf(x, window.innerWidth - popup.offsetWidth).coerceAtLeast(0).toString() + "px"
        popup.style.top = minOf(y, window.innerHeight - popup.offsetHeight).coerceAtLeast(0).toString() + "px"
        val buttons =
            (0 until popup.children.length)
                .mapNotNull {
                    popup.children.item(it) as? HTMLButtonElement
                }.filterNot { it.disabled }
        popup.addEventListener("keydown", { event: Event ->
            event as KeyboardEvent
            val index = buttons.indexOf(document.activeElement)
            val target =
                when (event.key) {
                    "ArrowDown" -> (index + 1) % buttons.size
                    "ArrowUp" -> (index - 1 + buttons.size) % buttons.size
                    "Home" -> 0
                    "End" -> buttons.lastIndex
                    else -> null
                }
            if (target != null) {
                event.preventDefault()
                buttons[target].focus()
            }
        })
        menu = popup
        buttons.firstOrNull()?.focus()
    }

    private fun element(
        tag: String,
        className: String?,
    ): HTMLElement =
        (document.createElement(tag) as HTMLElement).apply { if (className != null) this.className = className }

    private fun closeMenu(restoreFocus: Boolean = false) {
        menu?.remove()
        menu = null
        if (restoreFocus) menuOwner?.focus()
        menuOwner = null
    }

    private fun copyText(text: String) {
        val clipboard = window.navigator.asDynamic().clipboard
        if (clipboard != null) {
            clipboard.writeText(text).catch { _: dynamic -> reportError("Cannot access the clipboard") }
        } else {
            val input = element("textarea", null) as HTMLTextAreaElement
            input.value = text
            document.body!!.appendChild(input)
            input.select()
            if (!document.execCommand("copy")) reportError("Cannot access the clipboard")
            input.remove()
        }
    }
}
