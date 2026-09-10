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

    val mode: dynamic = js("({})")
    mode.getTokenizer = { tokenizer }
    mode.toggleCommentLines = { }
    mode.getNextLineIndent = { "" }
    mode.checkOutdent = { false }
    // Without this, Ace tries to spin up a background linting worker for the mode and logs a caught
    // "createWorker is not a function" warning; :gui already supplies diagnostics via setAnnotations.
    mode.createWorker = { null }
    return mode
}
