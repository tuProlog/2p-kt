package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.SyntaxAnalysis
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice
import org.fife.ui.rsyntaxtextarea.parser.ParseResult
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice

/** Converts parser-impl diagnostics into RSTA squiggle-underlined notices. */
internal class PrologSyntaxParser(
    private val analysis: () -> SyntaxAnalysis,
) : AbstractParser() {
    override fun parse(
        document: RSyntaxDocument,
        style: String,
    ): ParseResult {
        val result = DefaultParseResult(this)
        val root = document.defaultRootElement
        result.setParsedLines(0, (root.elementCount - 1).coerceAtLeast(0))
        for (diagnostic in analysis().diagnostics) {
            val range = diagnostic.range ?: continue
            val offset = range.start.offset.coerceIn(0, document.length)
            val length = (range.endExclusive.offset - offset).coerceAtLeast(1).coerceAtMost(document.length - offset)
            val notice = DefaultParserNotice(this, diagnostic.message, root.getElementIndex(offset), offset, length)
            notice.level = diagnostic.severity.toParserLevel()
            result.addNotice(notice)
        }
        return result
    }

    private fun DiagnosticSeverity.toParserLevel(): ParserNotice.Level =
        when (this) {
            DiagnosticSeverity.ERROR -> ParserNotice.Level.ERROR
            DiagnosticSeverity.WARNING -> ParserNotice.Level.WARNING
            DiagnosticSeverity.INFO -> ParserNotice.Level.INFO
        }
}
