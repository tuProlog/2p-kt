package it.unibo.tuprolog.ui.web.ace

/** One classified span of a tokenized line, as Ace's tokenizer API expects it. */
external interface AceToken {
    var type: String
    var value: String
}

/** Builds an [AceToken] as a plain JS object literal, matching the shape Ace expects at runtime. */
@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun aceToken(
    type: String,
    value: String,
): AceToken {
    val token: dynamic = js("({})")
    token.type = type
    token.value = value
    return token as AceToken
}
