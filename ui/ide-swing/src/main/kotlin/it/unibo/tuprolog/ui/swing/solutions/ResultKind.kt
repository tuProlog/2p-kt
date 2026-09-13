package it.unibo.tuprolog.ui.swing.solutions

/**
 * Which of the three [it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation] cases a result row shows, so
 * [SolutionCellRenderer] can pick its icon without depending on the presentation type itself.
 */
internal enum class ResultKind { YES, NO, HALT }
