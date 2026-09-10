package it.unibo.tuprolog.ui.web.ace

/**
 * The subset of Ace's `EditSession` API (https://ace.c9.io/#nav=api&api=edit_session) this IDE actually uses.
 * Obtained only via [AceEditor.session]; never constructed directly from Kotlin.
 */
external class AceEditSession {
    fun setMode(mode: String)

    fun setAnnotations(annotations: Array<AceAnnotation>)

    fun clearAnnotations()

    fun on(
        event: String,
        callback: (dynamic) -> Unit,
    )
}
