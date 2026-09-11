package it.unibo.tuprolog.ui.gui.presentation

/** Well-known [DiagnosticSource]s used to keep independently-produced diagnostics from clobbering each other. */
object DiagnosticSources {
    val SYNTAX = DiagnosticSource("syntax")
    val SOLVER = DiagnosticSource("solver")
}
