package it.unibo.tuprolog.ui.web.ace

/**
 * Builds an Ace "Mode" exposing only a tokenizer built from [getTokensForLine] — reusing `:gui`'s own
 * parser/lexer-backed classification instead of Ace's own regex-rule-based `TextHighlightRules`. Every other
 * method `EditSession`/`Editor` may call on a mode (comment toggling, auto-indent, ...) is a harmless no-op.
 *
 * Passed directly to [AceEditSession.setMode] as an object, bypassing Ace's `ace/mode/...` module registry
 * entirely (see [Ace] for why that registry isn't usable from a webpack bundle here).
 */
fun aceCustomMode(getTokensForLine: (row: Int, line: String) -> Array<AceToken>): dynamic {
    val tokenizer: dynamic = js("({})")
    tokenizer.getLineTokens = { line: String, _: dynamic, row: Int ->
        val result: dynamic = js("({})")
        result.tokens = getTokensForLine(row, line)
        result.state = ""
        result
    }

    // A Kotlin lambda with an empty/`Unit` body compiles to a function returning the Kotlin `Unit` singleton,
    // not JS `undefined` — and `Unit` is a truthy JS object. Ace treats a truthy transformAction/autoOutdent/...
    // result as "this call actually changed something" (e.g. `s = transformAction(...); s && (... e = s.text)`),
    // so a `Unit`-returning stub gets read as a real result whose (nonexistent) `.text` is `undefined`,
    // silently replacing the text Ace was about to insert. Every no-op below must return real JS `undefined`.
    val noop: dynamic = { js("undefined") }

    val mode: dynamic = js("({})")
    mode.getTokenizer = { tokenizer }
    mode.toggleCommentLines = noop
    mode.toggleBlockComment = noop
    mode.getNextLineIndent = { "" }
    mode.checkOutdent = { false }
    mode.autoOutdent = noop
    // Ace's Editor calls mode.transformAction(...) directly and unconditionally on every insert/delete (its
    // bracket/quote auto-pairing "behaviour" feature) rather than checking it exists first, so every method
    // ace/mode/text's real TextMode has by default must be present here too, even as a no-op — otherwise
    // typing a single character throws "transformAction is not a function". Ace's own default implementation
    // only does anything when a `this.$behaviour` object has been configured, which we never set, so a plain
    // no-op is behaviorally identical to that default.
    mode.transformAction = noop
    mode.getCompletions = { emptyArray<dynamic>() }
    // Without this, Ace tries to spin up a background linting worker for the mode and logs a caught
    // "createWorker is not a function" warning; :gui already supplies diagnostics via setAnnotations.
    mode.createWorker = { null }
    return mode
}
