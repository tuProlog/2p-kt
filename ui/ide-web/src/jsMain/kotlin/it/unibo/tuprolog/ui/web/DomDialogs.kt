package it.unibo.tuprolog.ui.web

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

/** Shows a small modal overlay built by [build], which receives a callback to dismiss it. */
internal fun showOverlay(build: (close: () -> Unit) -> HTMLElement) {
    val backdrop = document.createElement("div") as HTMLElement
    backdrop.className = "overlay-backdrop"

    fun close() {
        backdrop.parentNode?.removeChild(backdrop)
    }
    backdrop.appendChild(build(::close))
    document.body?.appendChild(backdrop)
}

/** Lists [names] in a modal, invoking [onSelected] with the chosen one or [onCancel] if dismissed. */
internal fun showDocumentPicker(
    names: List<String>,
    onSelected: (String) -> Unit,
    onCancel: () -> Unit,
) {
    if (names.isEmpty()) {
        window.alert("No documents saved yet in this browser.")
        onCancel()
        return
    }
    showOverlay { close ->
        val box = document.createElement("div") as HTMLElement
        box.className = "dialog"
        val title = document.createElement("div") as HTMLElement
        title.className = "row"
        title.innerHTML = "<strong>Open from browser storage</strong>"
        box.appendChild(title)
        val list = document.createElement("ul") as HTMLElement
        names.forEach { name ->
            val item = document.createElement("li") as HTMLElement
            item.textContent = name
            item.addEventListener(
                "click",
                { _: Event ->
                    close()
                    onSelected(name)
                },
            )
            list.appendChild(item)
        }
        box.appendChild(list)
        box.appendChild(
            cancelRow {
                close()
                onCancel()
            },
        )
        box
    }
}

private fun cancelRow(onCancel: () -> Unit): HTMLElement {
    val row = document.createElement("div") as HTMLElement
    row.className = "row"
    val button = document.createElement("button") as HTMLElement
    button.textContent = "Cancel"
    button.addEventListener("click", { _: Event -> onCancel() })
    row.appendChild(button)
    return row
}
