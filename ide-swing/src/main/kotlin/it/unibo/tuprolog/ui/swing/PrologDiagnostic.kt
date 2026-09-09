package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.TextPosition
import it.unibo.tuprolog.ui.gui.presentation.TextRange

internal fun PrologSyntaxException.toDiagnostic(): Diagnostic =
    Diagnostic(
        severity = DiagnosticSeverity.ERROR,
        message = message ?: "Syntax error",
        range = TextRange(span.start.toTextPosition(), span.endExclusive.toTextPosition()),
    )

private fun SourcePosition.toTextPosition(): TextPosition = TextPosition(offset, line, column)
