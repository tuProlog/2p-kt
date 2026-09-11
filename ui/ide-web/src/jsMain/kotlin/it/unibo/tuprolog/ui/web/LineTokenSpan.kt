package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken

/** One classified span of a single rendered line; [category] is `null` for text no [SemanticToken] covers. */
data class LineTokenSpan(
    val text: String,
    val category: SemanticCategory?,
)

/**
 * Slices [tokens] (whose ranges are in absolute document line/column coordinates) down to the spans that fall
 * on line number [row] of [line], clipping multi-line tokens (e.g. block comments) to their portion of that
 * line, and filling every gap between/around them with an uncategorized span so the spans' concatenation
 * always equals [line] exactly — a syntax-highlighting host such as Ace requires that invariant.
 */
fun semanticTokensForLine(
    row: Int,
    line: String,
    tokens: List<SemanticToken>,
): List<LineTokenSpan> {
    if (line.isEmpty()) return emptyList()
    val spans =
        tokens
            .filter { it.range.start.line <= row && it.range.endExclusive.line >= row }
            .map { token ->
                val start = if (token.range.start.line == row) token.range.start.column else 0
                val end = if (token.range.endExclusive.line == row) token.range.endExclusive.column else line.length
                Triple(start.coerceIn(0, line.length), end.coerceIn(0, line.length), token.category)
            }.filter { it.first < it.second }
            .sortedBy { it.first }

    val result = mutableListOf<LineTokenSpan>()
    var cursor = 0
    for ((start, end, category) in spans) {
        if (start < cursor) continue
        if (start > cursor) result += LineTokenSpan(line.substring(cursor, start), null)
        result += LineTokenSpan(line.substring(start, end), category)
        cursor = end
    }
    if (cursor < line.length) result += LineTokenSpan(line.substring(cursor), null)
    return result
}
