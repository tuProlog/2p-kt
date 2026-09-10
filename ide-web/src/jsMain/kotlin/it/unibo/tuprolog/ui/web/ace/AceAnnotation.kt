package it.unibo.tuprolog.ui.web.ace

/** A gutter annotation, shown next to the line number it refers to (https://ace.c9.io/#nav=api&api=edit_session). */
external interface AceAnnotation {
    var row: Int
    var column: Int
    var text: String
    var type: String
}

/** Builds an [AceAnnotation] as a plain JS object literal, matching the shape Ace expects at runtime. */
fun aceAnnotation(
    row: Int,
    column: Int,
    text: String,
    type: String,
): AceAnnotation {
    val annotation: dynamic = js("({})")
    annotation.row = row
    annotation.column = column
    annotation.text = text
    annotation.type = type
    return annotation as AceAnnotation
}
