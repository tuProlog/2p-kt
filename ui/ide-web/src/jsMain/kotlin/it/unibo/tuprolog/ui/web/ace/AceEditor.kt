package it.unibo.tuprolog.ui.web.ace

/**
 * The subset of Ace's `Editor` API (https://ace.c9.io/#nav=api&api=editor) this IDE actually uses.
 * Obtained only via [Ace.edit]; never constructed directly from Kotlin.
 */
external class AceEditor {
    val session: AceEditSession

    fun getValue(): String

    fun setValue(
        text: String,
        cursorPos: Int = definedExternally,
    ): String

    fun on(
        event: String,
        callback: (dynamic) -> Unit,
    )

    fun resize(force: Boolean = definedExternally)

    fun setTheme(theme: String)

    fun setFontSize(fontSize: String)

    fun setReadOnly(readOnly: Boolean)

    fun isFocused(): Boolean

    fun focus()

    fun destroy()
}
